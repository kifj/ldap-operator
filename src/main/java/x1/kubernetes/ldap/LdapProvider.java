package x1.kubernetes.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapProvider implements LdapConstants {
  private static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
  private static final Logger LOG = LoggerFactory.getLogger(LdapProvider.class);
  private static final String GROUPS = "cn=groups";

  private String ldapUrl;
  private String searchBase;
  private String ldapUser;
  private String ldapPassword;

  public LdapProvider(String ldapUrl, String searchBase, String ldapUser, String ldapPassword) {
    this.ldapUrl = ldapUrl;
    this.searchBase = searchBase;
    this.ldapUser = ldapUser;
    this.ldapPassword = ldapPassword;
  }

  public User lookupUser(String uid) throws NamingException {
    LOG.debug("Performing LDAP search for {}", uid);
    DirContext ctx = createDirectoryContext();
    try {
      String searchFilter = "(&(objectClass=person)(uid=" + uid + "))";
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchControls);
      if (!answer.hasMore()) {
        return null;
      }
      return extractUser(answer.next());
    } finally {
      ctx.close();
    }
  }

  public Group lookupGroup(String name) throws NamingException {
    LOG.debug("Performing LDAP search for {}", name);
    DirContext ctx = createDirectoryContext();
    try {
      String searchFilter = "(&(objectClass=posixGroup)(cn=" + name + "))";
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchControls);
      if (!answer.hasMore()) {
        return null;
      }
      return extractGroup(answer.next());
    } finally {
      ctx.close();
    }
  }

  public Group createGroup(Group group) throws NamingException {
    LOG.debug("Create LDAP group for {}", group);
    DirContext ctx = createDirectoryContext();
    try {
      Attributes attributes = new BasicAttributes();
      Attribute objectClassAttr = new BasicAttribute(ATTR_OBJECT_CLASS);
      objectClassAttr.add(ATTR_TOP);
      objectClassAttr.add(ATTR_POSIX_GROUP);
      attributes.put(objectClassAttr);
      attributes.put(ATTR_CN, group.getCommonName());
      attributes.put(ATTR_GID, group.getGid());
      if (!group.getMembers().isEmpty()) {
        Attribute members = new BasicAttribute(ATTR_MEMBER_UID);
        group.getMembers().forEach(member -> members.add(member));
        attributes.put(members);
      }
      ctx.createSubcontext(getFullGroupName(group), attributes);
    } finally {
      ctx.close();
    }
    return group;
  }

  public boolean updateGroup(Group oldGroup, Group newGroup) throws NamingException {
    LOG.debug("Update LDAP group {} with {}", oldGroup, newGroup);
    DirContext ctx = createDirectoryContext();
    List<ModificationItem> items = new ArrayList<>();
    try {
      if (!StringUtils.equals(oldGroup.getGid(), newGroup.getGid())) {
        items.add(
            new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(ATTR_GID, newGroup.getGid())));
      }
      if (!StringUtils.equals(oldGroup.getDescription(), newGroup.getDescription())) {
        items.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
            new BasicAttribute(ATTR_DESCRIPTION, newGroup.getDescription())));
      }
      Attribute members = new BasicAttribute(ATTR_MEMBER_UID);
      if (!newGroup.getMembers().isEmpty()) {
        if (!Objects.equals(newGroup.getMembers(), oldGroup.getMembers())) {
          newGroup.getMembers().forEach(member -> members.add(member));
          items.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, members));
        }
      } else if (!oldGroup.getMembers().isEmpty()) {
        items.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, members));
      }
      if (!items.isEmpty()) {
        ctx.modifyAttributes(getFullGroupName(oldGroup), items.toArray(new ModificationItem[items.size()]));
      }
    } finally {
      ctx.close();
    }
    return !items.isEmpty();
  }

  public Group deleteGroup(Group group) throws NamingException {
    LOG.debug("Delete LDAP group for {}", group);
    DirContext ctx = createDirectoryContext();
    try {
      ctx.destroySubcontext(getFullGroupName(group));
    } finally {
      ctx.close();
    }
    return group;
  }

  private DirContext createDirectoryContext() throws NamingException {
    Properties env = new Properties();
    env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY);
    env.put(Context.PROVIDER_URL, ldapUrl);
    env.put("com.sun.jndi.ldap.connect.timeout", "5000");
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, ldapUser);
    env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
    return new InitialDirContext(env);
  }

  private User extractUser(SearchResult sr) throws NamingException {
    User user = new User();
    Attributes attr = sr.getAttributes();
    if (attr.get(ATTR_UID) != null) {
      user.setUid(attr.get(ATTR_UID).get().toString());
    }
    if (attr.get(ATTR_CN) != null) {
      user.setCommonName(attr.get(ATTR_CN).get().toString());
    }
    if (attr.get(ATTR_MAIL) != null) {
      user.setEmail(attr.get(ATTR_MAIL).get().toString());
    }
    if (attr.get(ATTR_GIVEN_NAME) != null) {
      user.setFirstName(attr.get(ATTR_GIVEN_NAME).get().toString());
    }
    if (attr.get(ATTR_SN) != null) {
      user.setLastName(attr.get(ATTR_SN).get().toString());
    }
    LOG.debug("Found LDAP user {}", user);
    return user;
  }

  private Group extractGroup(SearchResult sr) throws NamingException {
    Group group = new Group();
    Attributes attr = sr.getAttributes();
    if (attr.get(ATTR_CN) != null) {
      group.setCommonName(attr.get(ATTR_CN).get().toString());
    }
    if (attr.get(ATTR_GID) != null) {
      group.setGid(attr.get(ATTR_GID).get().toString());
    }
    if (attr.get(ATTR_DESCRIPTION) != null) {
      group.setDescription(attr.get(ATTR_DESCRIPTION).get().toString());
    }
    if (attr.get(ATTR_MEMBER_UID) != null) {
      NamingEnumeration<?> members = attr.get(ATTR_MEMBER_UID).getAll();
      while (members.hasMore()) {
        group.addMember(members.next().toString());
      }
    }
    LOG.debug("Found LDAP group {}", group);
    return group;
  }

  private String getGroupName(Group group) {
    return ATTR_CN + "=" + group.getCommonName();
  }

  private String getFullGroupName(Group group) {
    return StringUtils.join(Arrays.asList(getGroupName(group), GROUPS, searchBase), ',');
  }
}
