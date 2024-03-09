package x1.kubernetes.ldap;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private String uid;
  private String firstName;
  private String lastName;
  private String commonName;
  private String email;

  public User() {
  }

  public User(String uid, String firstName, String lastName, String commonName, String email) {
    super();
    this.uid = uid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.commonName = commonName;
    this.email = email;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return "<User cn=" + getCommonName() + ", uid=" + getUid() + ", email=" + getEmail() + ">";
  }
}
