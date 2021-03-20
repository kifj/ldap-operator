package x1.kubernetes.ldap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class LdapRoleResourceSpec {
  @JsonProperty("roles")
  private List<LdapRoleAssigmentResourceSpec> roles = new ArrayList<>();

  public List<LdapRoleAssigmentResourceSpec> getRoles() {
    return roles;
  }
  
  public void setRoles(List<LdapRoleAssigmentResourceSpec> roles) {
    this.roles = roles;
  }

}
