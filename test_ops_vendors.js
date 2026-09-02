const http = require('http');

const req = http.request({
  hostname: 'localhost',
  port: 8080,
  path: '/globaltrade/api/auth/login',
  method: 'POST',
  headers: { 'Content-Type': 'application/json' }
}, res => {
  let data = '';
  res.on('data', chunk => data += chunk);
  res.on('end', () => {
    const token = JSON.parse(data).data.token;
    
    const req2 = http.request({
      hostname: 'localhost',
      port: 8080,
      path: '/globaltrade/api/ops/vendors',
      method: 'GET',
      headers: {
        'Authorization': 'Bearer ' + token
      }
    }, res2 => {
      let data2 = '';
      res2.on('data', chunk => data2 += chunk);
      res2.on('end', () => {
        console.log("Status: " + res2.statusCode);
        console.log(data2.substring(0, 500));
      });
    });
    req2.end();
  });
});
req.write(JSON.stringify({username: 'ops1', password: 'Password123!'}));
req.end();
