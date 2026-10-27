/**
 * SwiftShip Logistics - Node.js 26.x Express Server
 *
 * Responsibilities:
 * 1. Serves the pre-built Angular SPA from ../frontend/dist/swiftship-frontend/browser
 * 2. Proxies all /api/* requests to the Spring Boot CXF JAX-RS backend on port 8080
 * 3. Handles client-side Angular routing by returning index.html for unknown paths
 *
 * Usage:
 *   cd server && npm install && npm start
 *   (Requires backend running on :8080 and frontend built via `npm run build` in /frontend)
 */

const express = require('express');
const path = require('path');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
const PORT = process.env.PORT || 3000;
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8080';
const DIST_PATH = path.resolve(__dirname, '../frontend/dist/swiftship-frontend/browser');

// ─────────────────────────────────────────────
// 1. API Reverse Proxy → Spring Boot Backend
// ─────────────────────────────────────────────
app.use(
  '/api',
  createProxyMiddleware({
    target: BACKEND_URL,
    changeOrigin: true,
    on: {
      error: (err, req, res) => {
        console.error('[Proxy Error]', err.message);
        res.status(502).json({
          errorCode: 'PROXY_ERROR',
          message: `Backend at ${BACKEND_URL} is unreachable. Ensure the Spring Boot server is running on port 8080.`,
          status: 502,
          timestamp: new Date().toISOString()
        });
      },
      proxyReq: (proxyReq, req) => {
        console.log(`[Proxy] ${req.method} ${req.path} → ${BACKEND_URL}${req.path}`);
      }
    }
  })
);

// ─────────────────────────────────────────────
// 2. Serve Angular SPA Static Files
// ─────────────────────────────────────────────
app.use(express.static(DIST_PATH));

// ─────────────────────────────────────────────
// 3. Fallback → Angular Client-Side Routing
// ─────────────────────────────────────────────
app.get('*', (req, res) => {
  const indexPath = path.join(DIST_PATH, 'index.html');
  res.sendFile(indexPath, (err) => {
    if (err) {
      res.status(404).send(
        '<h2>SwiftShip SPA not built yet.</h2>' +
        '<p>Please run <code>npm run build</code> inside the <code>frontend/</code> folder first, then restart this server.</p>'
      );
    }
  });
});

// ─────────────────────────────────────────────
// 4. Start Server
// ─────────────────────────────────────────────
app.listen(PORT, () => {
  console.log('');
  console.log('🚀 SwiftShip Logistics Node.js Host started');
  console.log(`   SPA:     http://localhost:${PORT}`);
  console.log(`   API:     http://localhost:${PORT}/api/*  → ${BACKEND_URL}/api/*`);
  console.log(`   Backend: ${BACKEND_URL}`);
  console.log('');
});
