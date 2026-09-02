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
    
    const orderReq = http.request({
      hostname: 'localhost',
      port: 8080,
      path: '/globaltrade/api/customers/orders',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    }, res2 => {
      let data2 = '';
      res2.on('data', chunk => data2 += chunk);
      res2.on('end', () => {
        console.log("Status: " + res2.statusCode);
        console.log(data2);
      });
    });
    
    orderReq.write(JSON.stringify({
      customerFullName: 'Test User',
      city: 'Test City',
      country: 'Test Country',
      orderDescription: 'Test Order',
      itemCount: 100,
      expectedTimeline: '2 Weeks'
    }));
    orderReq.end();
  });
});
req.write(JSON.stringify({username: 'customer1', password: 'Password123!'}));
req.end();
