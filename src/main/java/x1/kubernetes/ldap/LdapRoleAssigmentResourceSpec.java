package x1.kubernetes.ldap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class LdapRoleAssigmentResourceSpec {
  @JsonProperty("users")
  private List<String> users = new ArrayList<>();

  @JsonProperty("role")
  private String role;

  @JsonProperty("gid")
  private String gid;

  @JsonProperty("description")
  private String description;

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getGid() {
    return gid;
  }
  
  public void setGid(String gid) {
    this.gid = gid;
  }
  
  public List<String> getUsers() {
    return users;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }

}
