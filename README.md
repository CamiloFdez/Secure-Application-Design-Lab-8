# Secure Application Design - Lab 8

---

# Table of Contents
- [Project Description](#project-description)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Local Setup and Execution](#local-setup-and-execution)
- [API Endpoints](#api-endpoints)
- [AWS Deployment (Two EC2 Instances)](#aws-deployment-two-ec2-instances)
- [Apache VirtualHost Configuration](#apache-virtualhost-configuration)
- [Security Group and Networking Notes](#security-group-and-networking-notes)
- [Testing](#testing)
- [Common Issues and Troubleshooting](#common-issues-and-troubleshooting)
- [Video Demonstration](#video-demonstration)
- [Author](#author)

---

# Project Description

This project implements a secure web application using **Spring Boot** and **Spring Security**, with a static frontend for testing authentication and protected endpoints.

The lab focuses on:

1. Building secure REST endpoints.
2. Registering and authenticating users.
3. Protecting business endpoints with authentication.
4. Deploying the solution on AWS using a split architecture:
	 - **Frontend EC2**: Apache (HTTPS) serving static assets.
	 - **Backend EC2**: Spring Boot API service.

The final production flow uses Apache as a reverse proxy so that browser traffic remains HTTPS while backend communication is handled safely by the server layer.

---

# Features

- User registration endpoint.
- User login endpoint.
- Protected secure endpoints under `/api/secure/**`.
- HTTP Basic authentication for protected resources.
- Password hashing with BCrypt.
- Browser-based API tester frontend (`index.html` + `style.css`).
- Deployment-ready setup for AWS EC2 with Apache reverse proxy.

---

# Technologies Used

- Java 17
- Spring Boot 3.3.6
- Spring Security
- Spring Data JPA
- H2 Database (runtime)
- Maven
- Apache HTTP Server (httpd)
- AWS EC2
- HTTPS with Let's Encrypt (frontend domain)

---

# Project Structure

```text
src/
	main/
		java/com/arep/springserver/
			App.java
			controller/
				AuthController.java
				SecureController.java
			model/
				User.java
			repository/
				UserRepository.java
			security/
				SecurityConfig.java
			service/
				UserService.java
		resources/
			application.properties
			static/
				index.html
				style.css
	test/
		java/com/arep/springserver/
			AppTest.java
images/
pom.xml
README.md
```

---

# Architecture Overview

## 1) Local Architecture

```text
Browser
	-> Spring Boot app (port 9090 or 8080)
			-> static frontend (index.html)
			-> REST API (/api/auth, /api/secure)
```

## 2) AWS Production Architecture (Two EC2 Instances)

```text
Browser (HTTPS)
	-> Frontend EC2 (Apache + SSL)
			-> /api reverse proxy
					-> Backend EC2 (Spring Boot on 9090)
```

This architecture avoids browser mixed-content errors and keeps deployment modular.

---

# Local Setup and Execution

1. Clone the repository:

```bash
git clone https://github.com/CamiloFdez/Secure-Application-Design-Lab-8.git
cd Secure-Application-Design-Lab-8
```

2. Build:

```bash
mvn clean package
```

3. Run Spring Boot:

```bash
mvn spring-boot:run
```

Optional (custom port):

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

4. Open frontend:

```text
http://localhost:9090/index.html
```

### Local Evidence

![Local index](images/indexLocal.PNG)
![Local Spring running](images/springweb.PNG)
![Local endpoint test](images/localhostEndpoint.PNG)

---

# API Endpoints

## Public Endpoints

| Method | Endpoint              | Description          |
|--------|-----------------------|----------------------|
| POST   | `/api/auth/register`  | Register a new user  |
| POST   | `/api/auth/login`     | Validate credentials |

## Protected Endpoints

| Method | Endpoint                  | Description                  |
|--------|---------------------------|------------------------------|
| GET    | `/api/secure/hello`       | Greeting for authenticated user |
| GET    | `/api/secure/profile`     | Current authenticated profile |
| GET    | `/api/secure/users`       | List all users               |
| GET    | `/api/secure/users/{id}`  | Get user by id               |
| GET    | `/api/secure/status`      | Server status for authenticated user |

### Endpoint Evidence

![Endpoint 1](images/endpoint1.PNG)
![Endpoint 2](images/endpoint2.PNG)
![Endpoint 3](images/endpoint3.PNG)
![Endpoint 4](images/endpoint4.PNG)
![Endpoint 5](images/endpoint5.PNG)
![Endpoint 6](images/endpoint6.PNG)
![Endpoint 7](images/endpoint7.PNG)

---

# AWS Deployment (Two EC2 Instances)

## Instance Roles

1. **Frontend EC2**:
	 - Apache (`httpd`) installed.
	 - Serves static files from `/var/www/secureapp`.
	 - Terminates HTTPS.
	 - Proxies `/api` requests to backend.

2. **Backend EC2**:
	 - Runs Spring Boot app on port `9090`.
	 - Can optionally run Apache for local forwarding.

## Typical Deployment Steps

1. Build and push latest code.
2. Pull project on EC2.
3. Start backend Spring service.
4. Configure frontend Apache VirtualHost and SSL.
5. Validate API reachability through frontend domain.

### AWS Evidence

![Apache public IP](images/ipApacheAws.PNG)
![Spring public IP](images/ipSpringAws.PNG)
![Install httpd](images/installHttpd.PNG)
![DuckDNS IP update](images/duckIp.PNG)
![NS lookup](images/nslookup.PNG)
![Security Apache](images/seguridadApache.PNG)
![Security Spring](images/seguridadSpring.PNG)

---

# Apache VirtualHost Configuration

Below is the recommended **frontend EC2** configuration pattern:

```apache
<VirtualHost *:80>
		ServerName apachefrontendarep.duckdns.org
		DocumentRoot /var/www/secureapp

		RewriteEngine On
		RewriteRule ^ https://%{HTTP_HOST}%{REQUEST_URI} [R=301,L]
</VirtualHost>

<VirtualHost *:443>
		ServerName apachefrontendarep.duckdns.org
		DocumentRoot /var/www/secureapp

		SSLEngine on
		SSLCertificateFile /etc/letsencrypt/live/apachefrontendarep.duckdns.org/fullchain.pem
		SSLCertificateKeyFile /etc/letsencrypt/live/apachefrontendarep.duckdns.org/privkey.pem

		ProxyPreserveHost On
		ProxyTimeout 60

		# Backend EC2 target (recommended: private IP)
		ProxyPass /api http://springbackarep.duckdns.org:9090/api
		ProxyPassReverse /api http://springbackarep.duckdns.org:9090/api
</VirtualHost>
```

### Why this matters

- Browser only talks to HTTPS frontend domain.
- Apache handles backend forwarding.
- Prevents mixed-content and SSL protocol errors in browser.

### VirtualHost Evidence

![VirtualHost](images/virtualHost.PNG)
![HTTPS frontend](images/httpsWeb.PNG)

---

# Security Group and Networking Notes

## Frontend EC2 SG

- Allow inbound: `80`, `443` from internet.

## Backend EC2 SG

- Allow inbound: `9090` only from frontend EC2 (or frontend SG).
- Avoid exposing `9090` publicly in production.

## Important

If frontend is HTTPS, do not call backend directly from browser as `http://...:9090`.
Use frontend domain + `/api` so Apache proxies requests.

### Deployment / Networking Evidence

![Save instances](images/guardarInstancias.PNG)
![AWS endpoint 1](images/endpointaws1.PNG)
![AWS endpoint 2](images/endpointaws2.PNG)
![AWS endpoint 3](images/endpointaws3.PNG)
![AWS endpoint 4](images/endpointaws4.PNG)
![AWS endpoint 5](images/endpointaws5.PNG)
![AWS endpoint 6](images/endpointaws6.PNG)
![AWS endpoint 7](images/endpointaws7.PNG)

---

# Testing

Run tests locally:

```bash
mvn test
```

Manual testing can be done with:

- Browser frontend page (`index.html`)
- `curl`
- Postman

Useful example:

```bash
curl -i http://localhost:9090/api/auth/register \
	-X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"demo","password":"demo123"}'
```

---

# Common Issues and Troubleshooting

## 1) Mixed Content

Error:

```text
Mixed Content: page loaded over HTTPS but requested HTTP resource
```

Fix:

- Use `https://apachefrontendarep.duckdns.org` in frontend.
- Route API through Apache `/api` proxy.

## 2) `ERR_SSL_PROTOCOL_ERROR` on `:9090`

Cause:

- Backend port 9090 is HTTP, not HTTPS.

Fix:

- Do not call `https://...:9090` from browser.
- Use frontend HTTPS + proxy.

## 3) DNS/Let's Encrypt validation failures

DuckDNS may intermittently return DNS SERVFAIL for some records.
If DNS is unstable, keep SSL termination on frontend and use backend over private/internal route.

## 4) Backend unreachable

Check:

```bash
curl -i http://127.0.0.1:9090/api/auth/login \
	-X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"x","password":"y"}'
```

If this fails, Spring is not running or not listening on port 9090.

---

# Video Demonstration

YouTube:

https://youtu.be/m7O7i4KIAKQ

---

# Author

Camilo Fernández