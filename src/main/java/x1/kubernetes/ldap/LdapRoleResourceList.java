package x1.kubernetes.ldap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.fabric8.kubernetes.api.model.DefaultKubernetesResourceList;

@JsonSerialize
public class LdapRoleResourceList extends DefaultKubernetesResourceList<LdapRoleResource> {
  private static final long serialVersionUID = 1L;
}
