package x1.kubernetes.ldap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.utils.KubernetesSerialization;

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

  @Produces
  @Singleton
  KubernetesClient makeDefaultClient(@Named("namespace") String namespace) {
    return new KubernetesClientBuilder().withConfig(new ConfigBuilder().withNamespace(namespace).build()).build();
  }

  @Produces
  @Singleton
  MixedOperation<LdapRoleResource, LdapRoleResourceList, Resource<LdapRoleResource>> makeCustomLdapRoleResourceClient(
      KubernetesClient defaultClient, KubernetesSerialization kubernetesSerialization) {
    kubernetesSerialization.registerKubernetesResource("v1beta2", "LdapRole", LdapRoleResource.class);
    return defaultClient.resources(LdapRoleResource.class, LdapRoleResourceList.class);
  }
}