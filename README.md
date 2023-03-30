# <img src="https://upload.wikimedia.org/wikipedia/commons/a/ab/Swagger-logo.png" width="300">
## Forked from 
https://github.com/Sayi/swagger-diff

## Introduction
Spring swagger diff it is util and allow anyone - be it development team or your end consumers -to visualize and notify any changes from  API’s resources without having any of the implementation logic in place. It’s automatically check difference version of api documentation and show in page changes and send changes to telegram group or channel
```java
   <dependency>
         <groupId>uz.narzullayev.javohir</groupId>
         <artifactId>autoconfigure</artifactId>
         <version>1.0</version>
   </dependency>
```
This utility depend on dependency springdoc-openapi-ui
```java
   <dependency>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-ui</artifactId>
       <version>1.6.8</version>
   </dependency>
```
### Init config
![img.png](img.png)

```path``` : visual url of swagger-ui <br/>
```latest-doc-path``` : storage path of api documentation  <br/>
```path-web``` - visual url of swagger-changes  <br/>

### Telegram config
![img_2.png](img_2.png)

```api-key``` : api key telegram <br/>
```web-hook``` : your secured application url <br/>
```chat-id``` : telegram chat id <br/>

### Example of working
```Added new api```
![img_3.png](img_3.png)
![img_4.png](img_4.png)
![img_5.png](img_5.png)



