import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const port = Number(process.env.PORT || 4173);
const siteApiUpstream = (process.env.SITE_API_UPSTREAM || 'http://127.0.0.1:8081').replace(/\/+$/, '');
const types = {
  '.html': 'text/html; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.png': 'image/png',
  '.svg': 'image/svg+xml',
  '.txt': 'text/plain; charset=utf-8',
  '.xml': 'application/xml; charset=utf-8',
};

function resolveFile(pathname) {
  if (pathname === '/') return path.join(root, 'index.html');
  return path.join(root, pathname.slice(1));
}

function serveFile(pathname, res) {
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
}

function proxyNovidades(req, res, pathname) {
  const target = new URL(pathname + (req.url.includes('?') ? req.url.slice(req.url.indexOf('?')) : ''), siteApiUpstream);
  const upstream = http.request(
    target,
    { method: req.method, headers: { ...req.headers, host: target.host, accept: 'text/html' } },
    (upstreamRes) => {
      if (upstreamRes.statusCode >= 500) {
        upstreamRes.resume();
        serveFile('/novidades.html', res);
        return;
      }
      res.writeHead(upstreamRes.statusCode || 502, upstreamRes.headers);
      upstreamRes.pipe(res);
    },
  );
  upstream.on('error', () => {
    serveFile('/novidades.html', res);
  });
  req.pipe(upstream);
}

http
  .createServer((req, res) => {
    let pathname = decodeURIComponent(req.url.split('?')[0]);
    if (pathname.startsWith('/novidades/') && pathname !== '/novidades/') {
      proxyNovidades(req, res, pathname);
      return;
    }
    serveFile(pathname, res);
  })
  .listen(port, '127.0.0.1', () => {
    console.log(`http://127.0.0.1:${port} (novidades → ${siteApiUpstream})`);
  });
