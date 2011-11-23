var net = require('net'),
	app = require('http').createServer(handler),
	io = require('socket.io').listen(app),
	fs = require('fs'),
	HOST = '10.23.1.28',
	PORT = '4225',
	client = new net.Socket();

app.listen(1337);

function handler (req, res) {
  fs.readFile('index.html', function (err, data) {
    if (err) {
      res.writeHead(500);
      return res.end('Error loading index.html');
    }
    res.writeHead(200, {'Content-Type': 'text/html'});
    res.end(data);
  });
}

client.connect(PORT, HOST, function () {
	console.log('TCP-SOCKET: connected to hackatron server');
	io.sockets.on('connection', function (socket) {
		socket.emit('ready', { arg: 'join' });
		socket.on('join', function (data) {
			console.log('SOCKET.IO: user ' + data + ' joined');
			client.write('join ' + data + "\n3\n");
		});
		socket.on('move', function (data) {
			console.log('SOCKET.IO: move ' + data);
			client.write(data + "\n");
		});
	});
});
client.on('close', function () {
	console.log('connection closed');
	client.destroy();
});
