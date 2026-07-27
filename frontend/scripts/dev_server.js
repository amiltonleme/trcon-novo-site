import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const port = Number(process.env.PORT || 4173);
const types = {
  '.html': 'text/html; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.png': 'image/png',
  '.xml': 'application/xml; charset=utf-8',
};

function resolveFile(pathname) {
  if (pathname === '/') return path.join(root, 'index.html');
  if (pathname.startsWith('/novidades/') && pathname !== '/novidades.html') {
    return path.join(root, 'novidades.html');
  }
  return path.join(root, pathname.slice(1));
}

http
  .createServer((req, res) => {
    let pathname = decodeURIComponent(req.url.split('?')[0]);
    const file = resolveFile(pathname);

    if (!file.startsWith(root)) {
      res.writeHead(403);
      res.end('forbidden');
      return;
    }

    fs.readFile(file, (error, data) => {
      if (error) {
        res.writeHead(404);
        res.end('not found');
        return;
      }

      res.writeHead(200, {
        'Content-Type': types[path.extname(file)] || 'application/octet-stream',
      });
      res.end(data);
    });
  })
  .listen(port, '127.0.0.1', () => {
    console.log(`http://127.0.0.1:${port}`);
  });
