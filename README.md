# ldap-operator - kubernetes custom resource for LDAP groups

this projects syncs a CRD ``ldap-roles.kubernetes.x1`` to LDAP.
The K8s resources are in folder ``etc``.

The CRD defines LDAP roles (as POSIX groups) and assigns existing users to the role.
To deploy it to K8s you need to define the CRD, the cluster role and its binding:

 - ``ldap-roles-crd.yaml``
 - ``ldap-operator-role.yaml``
 - ``ldap-operator-role-binding.yaml``
 - ``ldap-operator-service-account.yaml``

and then deploy the operator with ``ldap-operator-deployment.yaml``. 
Adapt the environment variables there for the LDAP-Server.

``ldap-role-example.yaml`` shows an example:

```
apiVersion: kubernetes.x1/v1beta2
kind: LdapRole
metadata:
  name: wildfly-roles
  namespace: default
spec:
  roles:
  - role: wildfly-admins
    gid: 1100
    description: Wildfly Administrators
    users:
    - joe
    - sysadm
  - role: wildfly-users
    gid: 1101
    description: Wildfly Users
    users:
    - snowdog
    - joe
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvn compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvn package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a docker image

You can create a docker image using:
```shell script
./mvn clean package -Dquarkus.container-image.build=true
```
You can then execute your native executable with: `./target/ldap-operator-1.0.0-SNAPSHOT-runner`

Or even a native executable using: 

```shell script
./mvn package -Pnative
```
