package x1.kubernetes.ldap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;

public class KubernetesClientProducer {
  @Produces
  @Singleton
  @Named("namespace")
  String findMyCurrentNamespace() throws IOException {
    Path path = Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace");
    if (Files.exists(path)) {
      return new String(Files.readAllBytes(path));
    }
    return "default";
  }

  @SuppressWarnings("resource")
  @Produces
  @Singleton
  KubernetesClient makeDefaultClient(@Named("namespace") String namespace) {
    return new DefaultKubernetesClient().inNamespace(namespace);
  }

  @Produces
  @Singleton
  MixedOperation<LdapRoleResource, LdapRoleResourceList, Resource<LdapRoleResource>> makeCustomLdapRoleResourceClient(
      KubernetesClient defaultClient) {
    KubernetesDeserializer.registerCustomKind("LdapRole", LdapRoleResource.class);
    return defaultClient.customResources(LdapRoleResource.class, LdapRoleResourceList.class);
  }
}