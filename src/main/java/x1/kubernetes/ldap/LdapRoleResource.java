package x1.kubernetes.ldap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Singular;
import io.fabric8.kubernetes.model.annotation.Version;

@JsonDeserialize
@Group("kubernetes.x1")
@Version("v1beta2")
@Kind("LdapRole")
@Plural("ldap-roles")
@Singular("ldap-role")
@ShortNames("lr")
public class LdapRoleResource extends CustomResource<LdapRoleResourceSpec, LdapRoleResourceStatus>
    implements Namespaced {
  private static final long serialVersionUID = 1L;

  private LdapRoleResourceSpec spec;
  private LdapRoleResourceStatus status;

  public LdapRoleResourceSpec getSpec() {
    return spec;
  }

  public void setSpec(LdapRoleResourceSpec spec) {
    this.spec = spec;
  }

  public LdapRoleResourceStatus getStatus() {
    return status;
  }

  public void setStatus(LdapRoleResourceStatus status) {
    this.status = status;
  }
}