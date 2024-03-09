package x1.kubernetes.ldap;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class Group implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private String commonName;
  private String description;
  private String gid;
  private Set<String> members;

  public Group() {
    this.members = new TreeSet<>();
  }

  public Group(String commonName, String gid) {
    this.commonName = commonName;
    this.gid = gid;
    this.members = new TreeSet<>();
  }

  public Group(String commonName, String gid, Set<String> members) {
    this.commonName = commonName;
    this.gid = gid;
    this.members = members;
  }

  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<String> getMembers() {
    return members;
  }

  public void setMembers(Set<String> members) {
    this.members = members;
  }

  public void addMember(String member) {
    members.add(member);
  }

  @Override
  public String toString() {
    return "Group [commonName=" + commonName + ", description=" + description + ", gid=" + gid + ", members=" + members
        + "]";
  }

}
