# Enterprise Architecture Workshop: Secure Application Design

## Descripción del Proyecto

Este proyecto implementa una aplicación segura y escalable desplegada en AWS, siguiendo las mejores prácticas de arquitectura empresarial y seguridad. La aplicación está compuesta por dos servidores independientes que trabajan en conjunto para proporcionar una solución integral y segura.

### Componentes de la Arquitectura

#### Servidor 1: Apache HTTP Server
- **Función**: Servidor web responsable de servir el cliente HTML+JavaScript asíncrono
- **Seguridad**: Configurado con conexión HTTPS/TLS para garantizar la integridad y confidencialidad de los datos
- **Certificados**: Utiliza certificados SSL/TLS generados a través de Let's Encrypt

#### Servidor 2: Spring Framework Server
- **Función**: Servidor backend que proporciona servicios RESTful API
- **Seguridad**: Protegido con TLS para comunicación segura cliente-servidor
- **Autenticación**: Sistema de login con contraseñas almacenadas de forma segura mediante hashing

### Características de Seguridad Implementadas

- **Cifrado TLS**: Transmisión segura de datos utilizando certificados Let's Encrypt
- **Cliente Asíncrono**: Implementación HTML+JavaScript con técnicas asíncronas para optimizar el rendimiento
- **Autenticación Segura**: Sistema de login con almacenamiento seguro de contraseñas mediante hashing
- **Despliegue en AWS**: Infraestructura segura y confiable en la nube de Amazon

## Video Demostrativo
```
https://pruebacorreoescuelaingeduco-my.sharepoint.com/:v:/g/personal/juan_brodriguez_mail_escuelaing_edu_co/EW8qAc6Q31BMrz95rLQ2Lw4B9A_GI0gwnJBj2GmI56Y4dA?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=5GidDS
```

## Requisitos del Sistema

- Java 17 o superior
- Maven 3.5+ (incluye wrapper mvnw)
- IDE o editor de texto (IntelliJ IDEA, VSCode, Spring Tools Suite, Eclipse)
- Cuenta activa de AWS
- Acceso a terminal/línea de comandos

## Instalación y Configuración

### 1. Clonación del Repositorio

```bash
git clone https://github.com/juan-beltran0518/ArepSecureApplicationDesign.git
cd ArepSecureApplicationDesign
```

### 2. Configuración de Grupos de Seguridad en AWS

Se requieren dos grupos de seguridad independientes para cada servidor:

#### ApacheSecurityGroup
**Reglas de entrada:**
- SSH: Origen Mi IP
- HTTP: Cualquier IPv4 (0.0.0.0/0)
- HTTPS: Cualquier IPv4 (0.0.0.0/0)

![Apache Security Group](resources/1.png)

#### SpringSecurityGroup
**Reglas de entrada:**
- SSH: Origen Mi IP
- HTTP: Cualquier IPv4 (0.0.0.0/0)
- HTTPS: Cualquier IPv4 (0.0.0.0/0)
- Puerto 8443: Cualquier IPv4 (para Spring Boot HTTPS)

![Spring Security Group](resources/2.png)

### 3. Creación de Instancias EC2

#### Instancia Apache Server
- **Tipo de instancia**: t2.micro
- **Sistema operativo**: Amazon Linux 2023
- **Grupo de seguridad**: ApacheSecurityGroup
- **Par de claves**: Crear nuevo par de claves

![Apache Server Configuration](resources/3.png)
![Apache Server Details](resources/4.png)

#### Instancia Spring Server
- **Tipo de instancia**: t2.micro
- **Sistema operativo**: Amazon Linux 2023
- **Grupo de seguridad**: SpringSecurityGroup
- **Par de claves**: Crear nuevo par de claves

![Spring Server Configuration](resources/5.png)
![Spring Server Details](resources/6.png)

### 4. Configuración DNS

#### Obtención de IPs Públicas
1. Copiar la IP pública de cada instancia desde la consola de AWS

![Apache Server IP](resources/7.png)
![Spring Server IP](resources/8.png)

#### Configuración en DuckDNS
1. Acceder a [DuckDNS](https://www.duckdns.org/domains)
2. Vincular cada IP con un dominio personalizado

![DNS Configuration](resources/9.png)

#### Verificación DNS
Verificar la resolución DNS desde terminal local:

```bash
nslookup appivanarep.duckdns.org
nslookup springivanarep.duckdns.org
```

![DNS Verification](resources/10.png)

### 5. Configuración del Servidor Apache

#### Conexión SSH y Configuración Inicial

```bash
ssh -i "apache-key.pem" ec2-user@[IP_PUBLICA_APACHE]
```

![Apache SSH Connection](resources/11.png)

#### Actualización del Sistema e Instalación de Apache

```bash
sudo dnf upgrade -y
sudo dnf install -y httpd
sudo systemctl enable --now httpd
sudo systemctl status httpd
```

![Apache Installation](resources/12.png)

#### Configuración de Permisos

```bash
sudo usermod -a -G apache ec2-user
sudo chown -R ec2-user:apache /var/www
sudo chmod 2775 /var/www && find /var/www -type d -exec sudo chmod 2775 {} \;
find /var/www -type f -exec sudo chmod 0664 {} \;
```

![Apache Permissions](resources/13.png)

#### Instalación y Configuración de Certificados SSL

```bash
sudo dnf install -y python3-certbot-apache
sudo systemctl stop httpd
sudo certbot-3 certonly --standalone -d appivanarep.duckdns.org \
  -m tu-email@ejemplo.com --agree-tos --no-eff-email
```

![SSL Certificate Installation](resources/14.png)
![SSL Certificate Details](resources/15.png)

#### Configuración SSL en Apache

```bash
sudo nano /etc/httpd/conf.d/ssl.conf
```

Actualizar las rutas de certificados:

![SSL Configuration](resources/16.png)

#### Verificación del Servicio

```bash
sudo systemctl start httpd
sudo systemctl enable httpd
sudo systemctl status httpd
```

![Apache Service Status](resources/17.png)

Verificación en navegador:

![HTTPS Verification](resources/18.png)

### 6. Configuración del Servidor Spring

#### Conexión SSH y Configuración Inicial

```bash
ssh -i "spring-key.pem" ec2-user@[IP_PUBLICA_SPRING]
sudo dnf upgrade -y
```

#### Instalación de Java y Maven

```bash
sudo dnf install -y java-21-amazon-corretto unzip maven
java -version
mvn -version
```

![Java Installation](resources/19.png)

#### Configuración de Versión Java

```bash
sudo alternatives --config java
sudo alternatives --config javac
```

![Java Version Configuration](resources/20.png)

#### Instalación de Certificados SSL

```bash
sudo dnf install -y python3-certbot python3-certbot-apache
sudo certbot-3 certonly --standalone \
  -d springivanarep.duckdns.org \
  -m tu-email@ejemplo.com --agree-tos -n
```

![Spring SSL Certificate](resources/21.png)

#### Generación del Proyecto Spring

```bash
mvn archetype:generate \
  -DgroupId=com.arep.springserver \
  -DartifactId=springserver \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false
```

![Project Scaffolding](resources/22.png)

#### Configuración del Proyecto

1. **Modificación del pom.xml**

![POM Configuration](resources/23.png)

2. **Creación de la clase Application**

![Application Class](resources/24.png)

3. **Compilación inicial**

```bash
mvn clean package -DskipTests
```

![Initial Compilation](resources/25.png)

#### Configuración HTTPS en Spring

1. **Creación del directorio keystore**

```bash
sudo mkdir -p /opt/springserver
sudo chown ec2-user:ec2-user /opt/springserver
```

2. **Conversión de certificados a formato PKCS#12**

```bash
sudo openssl pkcs12 -export \
  -in /etc/letsencrypt/live/springivanarep.duckdns.org/fullchain.pem \
  -inkey /etc/letsencrypt/live/springivanarep.duckdns.org/privkey.pem \
  -out /opt/springserver/keystore.p12 \
  -name tomcat \
  -password pass:root1234
```

![Keystore Creation](resources/26.png)

3. **Configuración application.properties**

```bash
mkdir -p src/main/resources
nano src/main/resources/application.properties
```

![Application Properties](resources/27.png)

#### Estructura de la API

Creación de la estructura de paquetes:

```bash
mkdir -p src/main/java/com/arep/springserver/controller
mkdir -p src/main/java/com/arep/springserver/model
mkdir -p src/main/java/com/arep/springserver/repository
mkdir -p src/main/java/com/arep/springserver/security
mkdir -p src/main/java/com/arep/springserver/service
```

![Project Structure](resources/28.png)

#### Implementación de Componentes

1. **Controllers**: AuthController.java y SecureController.java
2. **Models**: User.java
3. **Repository**: UserRepository.java
4. **Security**: SecurityConfig.java
5. **Service**: UserService.java

![Controller Implementation](resources/29.png)

#### Configuración Base de Datos H2

Actualización de application.properties con configuración H2:

![H2 Configuration](resources/30.png)

#### Compilación y Ejecución

```bash
mvn -U clean package -DskipTests
java -jar target/*.jar
```

![Final Compilation](resources/31.png)

## Pruebas y Verificación

### Pruebas de API con Postman

1. **Registro de usuario**
2. **Autenticación con Basic Auth**

![Postman Tests](resources/32.png)

### Pruebas en Navegador

Verificación de códigos de respuesta y funcionalidad:

![Browser Tests](resources/33.png)

### Verificación de Persistencia

Acceso a consola H2:
- URL: `https://springivanarep.duckdns.org:8443/h2-console`
- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:file:/opt/springserver/data/testdb`
- User Name: `sa`
- Password: (en blanco)

![Database Verification](resources/34.png)

## Documentación Técnica

### Arquitectura de Seguridad

La aplicación implementa múltiples capas de seguridad:

1. **Capa de Transporte**: Cifrado TLS/HTTPS en ambos servidores
2. **Capa de Aplicación**: Autenticación y autorización con Spring Security
3. **Capa de Datos**: Hashing seguro de contraseñas con BCrypt
4. **Capa de Infraestructura**: Grupos de seguridad AWS configurados apropiadamente


## Autor

**Juan Beltrán** - [juan-beltran0518](https://github.com/juan-beltran0518)



## Referencias

- [AWS EC2 LAMP Stack Guide](https://docs.aws.amazon.com/linux/al2023/ug/ec2-lamp-amazon-linux-2023.html)
- [Spring Security Guide](https://spring.io/guides/gs/securing-web)
- [Let's Encrypt Documentation](https://letsencrypt.org/docs/)
- [Spring Boot SSL Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver.configure-ssl)

