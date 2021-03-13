curl -X GET http://localhost:8080/employees && echo
curl -X PUT -H 'Content-type:application/json' http://localhost:8080/employees/3 -d '{"name":"Sample User3","role":"Sample Role-3"}'
curl -X POST -H 'Content-type:application/json' http://localhost:8080/employees -d '{"name":"Test User3","role":"Role-3"}'
curl -X DELETE http://localhost:8080/employees/3 && echo
