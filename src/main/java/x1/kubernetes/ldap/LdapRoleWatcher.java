package x1.kubernetes.ldap;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.quarkus.runtime.StartupEvent;

public class LdapRoleWatcher {
  private static final Logger LOG = LoggerFactory.getLogger(LdapRoleWatcher.class);

  @Inject
  KubernetesClient client;

  @Inject
  MixedOperation<LdapRoleResource, LdapRoleResourceList, Resource<LdapRoleResource>> crClient;

  @Inject
  @ConfigProperty(name = "ldap.search-base")
  String searchBase;

  @Inject
  @ConfigProperty(name = "ldap.user")
  String ldapUser;

  @Inject
  @ConfigProperty(name = "ldap.url")
  String ldapUrl;

  @Inject
  @ConfigProperty(name = "ldap.password")
  String ldapPassword;

  void onStartup(@Observes StartupEvent event) {
    LOG.info("Starting to watch LdapRoleResource in namespace " + client.getNamespace() + " [" + client.getMasterUrl()
        + "]");

    crClient.watch(new Watcher<LdapRoleResource>() {
      @Override
      public void eventReceived(Action action, LdapRoleResource resource) {
        final String name = resource.getMetadata().getName();
        final List<LdapRoleAssigmentResourceSpec> roles = resource.getSpec().getRoles();
        LdapProvider ldapProvider = new LdapProvider(ldapUrl, searchBase, ldapUser, ldapPassword);
        LOG.info("Received {} for {}", action, name);
        boolean changed = false;
        try {
          for (LdapRoleAssigmentResourceSpec role : roles) {
            boolean roleChanged = updateRoleAssigment(action, name, ldapProvider, role);
            changed = changed || roleChanged;
          }
          if (changed) {
            LdapRoleResourceStatus status = new LdapRoleResourceStatus();
            status.setMessage("Applied action " + action);
            resource.setStatus(status);
            crClient.updateStatus(resource);
          }
        } catch (Exception e) {
          LOG.error(null, e);
          LdapRoleResourceStatus status = new LdapRoleResourceStatus();
          status.setMessage("Error for action " + action + ": " + e.getMessage());
          resource.setStatus(status);
          crClient.updateStatus(resource);
        }
      }

      private boolean updateRoleAssigment(Action action, final String name, LdapProvider ldapProvider,
          LdapRoleAssigmentResourceSpec role) throws NamingException {
        final String roleName = role.getRole();
        final String gid = role.getGid();
        final String description = role.getDescription();
        final List<String> users = role.getUsers();

        Group group = ldapProvider.lookupGroup(roleName);

        boolean changed = false;
        switch (action) {
        case ADDED:
        case MODIFIED:
          Set<String> existingUsers = lookupUsers(ldapProvider, users);

          if (group == null) {
            Group newGroup = new Group(roleName, gid, existingUsers);
            newGroup.setDescription(description);
            LOG.info("Create new {} for role {}", newGroup, roleName);
            group = ldapProvider.createGroup(newGroup);
            changed = true;
          } else {
            Group newGroup = new Group(group.getCommonName(), gid, existingUsers);
            newGroup.setDescription(description);
            changed = ldapProvider.updateGroup(group, newGroup);
            if (changed) {
              LOG.info("Update {} for role {}", newGroup, roleName);
            }
          }

          break;
        case DELETED:
          if (group != null) {
            LOG.info("Delete {} for role {}", group, role);
            ldapProvider.deleteGroup(group);
            changed = true;
          } else {
            LOG.info("Group {} does not exist, delete is not necessary", role);
          }
          break;
        default:
          break;
        }
        return changed;
      }

      private Set<String> lookupUsers(LdapProvider ldapProvider, final List<String> users) throws NamingException {
        Set<String> existingUsers = new TreeSet<>();
        for (String user : users) {
          User found = ldapProvider.lookupUser(user);
          if (found != null) {
            LOG.debug("User {} found", found);
            existingUsers.add(found.getUid());
          } else {
            LOG.warn("User{} not found", user);
          }
        }
        return existingUsers;
      }

      @Override
      public void onClose(WatcherException e) {
        LOG.warn(e.getMessage());
        client.close();
      }
    });
  }
}
