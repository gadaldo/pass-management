# pass-management

Overview
---

This is a simple Java 8 application based on Spring boot.
It exposes restful API to manage passes for customer.

Features:

- create/delete customer;
- list existing customers;
- get customer info;

- add pass for selected customer;
- modify pass length;
- delete pass for given customer;

- a vendor can check if pass is expired, based on created date and pass length; _NOTE_ vendor id it's just mock, can be any string.

Requirements
---

- docker
- maven 3.2 or above
- java 8

Run
---

Place in the root folder: pass-management and rn following command line:

```bash
./run.sh
```

This will stop any running docker containers named mysql.
_NOTE_ make sure there are no other service on port 9090.

Sample http requests:
---

create customer:

```bash
curl -X POST localhost:9090/pass-management/customers -H 'Content-type:application/json' -d '{"name" : "John", "surname" : "Wood", "homeCity" : "London" }'
```

list existing customers:

```bash
http://localhost:9090/pass-management/customers/
```

get customer by id '1':

```bash
http://localhost:9090/pass-management/customers/1
```

list passes for customer '1':

```bash
http://localhost:9090/pass-management/customers/1/passes
```

delete customer '1':

```bash
curl -X DELETE localhost:9090/pass-management/customers/1
```

add pass for user '1':

```bash
curl -X POST localhost:9090/pass-management/customers/1/passes -H 'Content-type:application/json' -d '{"city" : "Naples", "lenght" : 3}'
```

amend pass '5' for user '1':

```bash
curl -X PUT localhost:9090/pass-management/customers/1/passes/5 -H 'Content-type:application/json' -d '{"city" : "Naples", "lenght" : 10}'
```

delete pass '2' for customer '1':

```bash
curl -X DELETE localhost:9090/pass-management/customers/1/passes/2
```

validate pass:

```bash
curl -X GET localhost:9090/pass-management/vendors/vendor_a/passes/3/validate
```

Further improvements
---
