package x1.kubernetes.ldap;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * helper class for native image builder, all classes listed here must be added to the 
 * native image with all constructors and methods
 */
@RegisterForReflection(classNames = {
    "com.sun.jndi.ldap.LdapCtxFactory"
})
public final class ReflectionHelper {
  private ReflectionHelper() {}
}
