package x1.kubernetes.ldap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.fabric8.kubernetes.client.CustomResourceList;

@JsonSerialize
public class LdapRoleResourceList extends CustomResourceList<LdapRoleResource> {
  private static final long serialVersionUID = 1L;
}
