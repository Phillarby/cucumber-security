# Cucumber-Zap

This is an example project using the cucumber BDD framework to scan and report on website vulnerabilities using 
the OWASP ZAP API.  

This is designed to be suitable for manual execution, or run as part of a CI/CD pipeline.  Please only run scans against 
sites you have permission to scan.

## Requirements:  
[OWASP ZAP](https://owasp.org/www-project-zap/) is running and the API is accessible.   
  
An easy way to achieve this using the [Zap2Docker](https://hub.docker.com/r/owasp/zap2docker-stable/) docker image.  
This project is already configured to use this version of ZAP with it's default configuration as per the run commands
below

An example docker run command to launch zap 
`docker run -u zap -p 8080:8080 -i owasp/zap2docker-stable zap.sh -daemon -host 0.0.0.0 -port 8080 -config api.addrs.addr.name=.\* -config api.addrs.addr.regex=true -config api.key={insert any API key here}}`

If you need to present a specific client certificate when accessing HTTPS pages, this can be added in the launch config
by mounting a local folder containing the cert in PKCS12 format into the container, and specifying the certificate details
as per the below
`docker run -v {local folder}:/zap/certs -u zap -p 8080:8080 -i owasp/zap2docker-stable zap.sh -daemon -host 0.0.0.0 -port 8080 -config api.addrs.addr.name=.\* -config api.addrs.addr.regex=true -config api.key=qwerty -config certificate.use=true -config certificate.pkcs12.path=/zap/certs/{Certificate Filename} -config certificate.pkcs12.password={Certificate Password} -config certificate.pkcs12.index=0 -config certificate.persist=false`

More details of this configuration are available in this [github commit](https://github.com/kingthorin/zap-core-help/commit/6433504b5c8649f3cc472a2f3ccd207834463c6f) 
