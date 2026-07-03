import { apiConfig } from './modules/config.js';
import {
  changeClass,
  escapeHtml,
  safeClass,
  safeCssColor,
  safeGradient,
  safePercent,
  safeUrl,
} from './modules/sanitize.js';
import { buildLeadPayload, submitLead, mensagemDeErro } from './modules/lead-form.js';
import { fetchWithFallback, buildHighlightsHtml, buildNewsHtml } from './modules/content.js';

const LEADS_API_URL = apiConfig.leadsApiUrl;

  // PAGE NAVIGATION
  function showPage(id) {
    const page = document.getElementById('page-' + id);
    if (!page) return;
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    page.classList.add('active');
    // Marca o link ativo pela correspondência de data-page-link (robusto a
    // reordenação/adição de itens de menu — não depende de índice fixo).
    document.querySelectorAll('.nav-links a').forEach(a => {
      a.classList.toggle('active', a.dataset.pageLink === id);
    });
    updateSubpageHeroAnimations();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // MOBILE NAV
  function openMobile() { document.getElementById('mobileNav').classList.add('open'); }
  function closeMobile() { document.getElementById('mobileNav').classList.remove('open'); }

  // Pré-seleciona o tipo de interesse no formulário quando o usuário chega por
  // um CTA que carrega data-lead-type (ex.: "Montar um time" -> ALOCACAO...).
  function preselectLeadType(tipo) {
    if (!tipo) return;
    const select = document.querySelector('#betaWaitlistForm select[name="tipoInteresse"]');
    if (select && [...select.options].some(o => o.value === tipo)) {
      select.value = tipo;
    }
  }

  function setupNavigation() {
    document.querySelectorAll('[data-page-link]').forEach(link => {
      link.addEventListener('click', event => {
        event.preventDefault();
        showPage(link.dataset.pageLink);
        preselectLeadType(link.dataset.leadType);
        if (link.hasAttribute('data-close-mobile')) closeMobile();
      });
    });

    document.querySelector('[data-mobile-open]')?.addEventListener('click', openMobile);
    document.querySelector('[data-mobile-close]')?.addEventListener('click', closeMobile);
  }

  function setupBetaWaitlist() {
    const form = document.getElementById('betaWaitlistForm');
    const success = document.getElementById('betaWaitlistSuccess');
    if (!form || !success) return;

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const submitButton = form.querySelector('button[type="submit"]');
      const originalLabel = submitButton ? submitButton.textContent : '';
      const entries = Object.fromEntries(new FormData(form).entries());
      const payload = buildLeadPayload(entries, { origem: 'site-trcon' });

      if (submitButton) {
        submitButton.disabled = true;
        submitButton.textContent = 'Enviando...';
      }

      try {
        await submitLead(LEADS_API_URL, payload);
        form.reset();
        success.hidden = false;
        success.scrollIntoView({ behavior: 'smooth', block: 'center' });
      } catch (error) {
        // Degradação previsível: o formulário mostra erro claro, a página não quebra.
        alert(mensagemDeErro(error));
      } finally {
        if (submitButton) {
          submitButton.disabled = false;
          submitButton.textContent = originalLabel || 'Entrar na fila de espera';
        }
      }
    });
  }

  function initHeroScene() {
    const canvas = document.getElementById('neuralCanvas');
    const hero = document.querySelector('.tr-hero');
    if (!canvas || !hero) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let width = 0;
    let height = 0;
    let nodes = [];
    const mouse = { x: -999, y: -999 };

    function resizeHeroCanvas() {
      const rect = hero.getBoundingClientRect();
      const ratio = window.devicePixelRatio || 1;
      width = Math.max(320, Math.floor(rect.width));
      height = Math.max(420, Math.floor(rect.height));
      canvas.width = Math.floor(width * ratio);
      canvas.height = Math.floor(height * ratio);
      canvas.style.width = width + 'px';
      canvas.style.height = height + 'px';
      ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
      const targetCount = Math.min(115, Math.max(42, Math.floor((width * height) / 14500)));
      nodes = Array.from({ length: targetCount }, () => new HeroNode(true));
    }

    class HeroNode {
      constructor(init = false) {
        this.reset(init);
      }

      reset(init = false) {
        this.x = Math.random() * width;
        this.y = init ? Math.random() * height : (Math.random() > 0.5 ? -20 : height + 20);
        this.vx = (Math.random() - 0.5) * 0.38;
        this.vy = (Math.random() - 0.5) * 0.38;
        this.r = Math.random() * 2.4 + 1;
        this.type = Math.random() > 0.68 ? 'gold' : 'cyan';
        this.alpha = Math.random() * 0.5 + 0.2;
        this.pulse = Math.random() * Math.PI * 2;
      }

      update() {
        this.x += this.vx;
        this.y += this.vy;
        this.pulse += 0.02;

        const dx = this.x - mouse.x;
        const dy = this.y - mouse.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0 && dist < 110) {
          this.vx += (dx / dist) * 0.25;
          this.vy += (dy / dist) * 0.25;
        }

        this.vx *= 0.99;
        this.vy *= 0.99;

        if (this.x < -30 || this.x > width + 30 || this.y < -30 || this.y > height + 30) {
          this.reset();
        }
      }

      draw() {
        const alpha = this.alpha * (0.7 + 0.3 * Math.sin(this.pulse));
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.r, 0, Math.PI * 2);
        if (this.type === 'gold') {
          ctx.fillStyle = `rgba(200,150,12,${alpha})`;
          ctx.shadowColor = '#c8960c';
        } else {
          ctx.fillStyle = `rgba(0,212,255,${alpha * 0.7})`;
          ctx.shadowColor = '#00d4ff';
        }
        ctx.shadowBlur = 8;
        ctx.fill();
        ctx.shadowBlur = 0;
      }
    }

    function drawConnections() {
      const maxDist = 150;
      for (let i = 0; i < nodes.length; i++) {
        for (let j = i + 1; j < nodes.length; j++) {
          const dx = nodes[i].x - nodes[j].x;
          const dy = nodes[i].y - nodes[j].y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < maxDist) {
            const alpha = (1 - dist / maxDist) * 0.24;
            const isGold = nodes[i].type === 'gold' && nodes[j].type === 'gold';
            ctx.beginPath();
            ctx.moveTo(nodes[i].x, nodes[i].y);
            ctx.lineTo(nodes[j].x, nodes[j].y);
            ctx.strokeStyle = isGold ? `rgba(200,150,12,${alpha})` : `rgba(0,212,255,${alpha * 0.62})`;
            ctx.lineWidth = 0.6;
            ctx.stroke();
          }
        }
      }
    }

    function animateHero() {
      const page = hero.closest('.page');
      const shouldRender = !document.hidden && (!page || page.classList.contains('active'));
      if (shouldRender) {
        ctx.clearRect(0, 0, width, height);
        drawConnections();
        nodes.forEach(node => {
          node.update();
          node.draw();
        });
      }
      requestAnimationFrame(animateHero);
    }

    function updateHeroClock() {
      const clock = document.getElementById('heroClock');
      if (!clock) return;
      const now = new Date();
      clock.textContent = [
        String(now.getHours()).padStart(2, '0'),
        String(now.getMinutes()).padStart(2, '0'),
        String(now.getSeconds()).padStart(2, '0')
      ].join(':');
    }

    const dataStrings = [
      'LOADING AI MODEL...', 'NEURAL NET V4.2', 'RISK ANALYSIS OK',
      'API CONNECTED', 'DATA PIPELINE ACTIVE', 'ML TRAINING 98%',
      'PORTFOLIO OPTIMIZED', 'LATENCY: 12ms', 'MARKET SIGNAL ON',
      'SECURITY: AES-256', 'NODES: 12.408', 'UPTIME: 99.97%'
    ];

    function spawnDataNode() {
      const el = document.createElement('div');
      const duration = 8 + Math.random() * 7;
      el.className = 'tr-data-node';
      el.textContent = dataStrings[Math.floor(Math.random() * dataStrings.length)];
      el.style.left = Math.random() * 86 + 7 + '%';
      el.style.bottom = Math.random() * 28 + 12 + '%';
      el.style.animationDuration = duration + 's';
      hero.appendChild(el);
      setTimeout(() => el.remove(), duration * 1000);
    }

    hero.addEventListener('mousemove', event => {
      const rect = hero.getBoundingClientRect();
      mouse.x = event.clientX - rect.left;
      mouse.y = event.clientY - rect.top;
    });
    hero.addEventListener('mouseleave', () => {
      mouse.x = -999;
      mouse.y = -999;
    });

    resizeHeroCanvas();
    window.addEventListener('resize', resizeHeroCanvas);
    updateHeroClock();
    setInterval(updateHeroClock, 1000);
    setInterval(spawnDataNode, 2600);
    spawnDataNode();
    animateHero();
  }

  const subpageHeroScenes = [];

  function initSubpageHeroScenes() {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

    document.querySelectorAll('.subpage-tr-hero').forEach(hero => {
      const canvas = hero.querySelector('.subpage-neural-canvas');
      if (!canvas) return;
      const ctx = canvas.getContext('2d');
      if (!ctx) return;

      let width = 0;
      let height = 0;
      let rafId = null;
      let nodes = [];
      const mouse = { x: -999, y: -999 };

      class SubpageNode {
        constructor(init = false) {
          this.reset(init);
        }

        reset(init = false) {
          this.x = Math.random() * width;
          this.y = init ? Math.random() * height : Math.random() * height * 0.4 - 40;
          this.vx = (Math.random() - 0.5) * 0.24;
          this.vy = (Math.random() - 0.5) * 0.24;
          this.r = Math.random() * 1.9 + 0.8;
          this.gold = Math.random() > 0.72;
          this.alpha = Math.random() * 0.38 + 0.16;
          this.pulse = Math.random() * Math.PI * 2;
        }

        update() {
          this.x += this.vx;
          this.y += this.vy;
          this.pulse += 0.018;

          const dx = this.x - mouse.x;
          const dy = this.y - mouse.y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist > 0 && dist < 95) {
            this.vx += (dx / dist) * 0.12;
            this.vy += (dy / dist) * 0.12;
          }

          this.vx *= 0.992;
          this.vy *= 0.992;

          if (this.x < -24 || this.x > width + 24 || this.y < -24 || this.y > height + 24) {
            this.reset();
          }
        }

        draw() {
          const alpha = this.alpha * (0.72 + 0.28 * Math.sin(this.pulse));
          ctx.beginPath();
          ctx.arc(this.x, this.y, this.r, 0, Math.PI * 2);
          ctx.fillStyle = this.gold ? `rgba(240,180,41,${alpha})` : `rgba(0,212,255,${alpha})`;
          ctx.shadowColor = this.gold ? '#f0b429' : '#00d4ff';
          ctx.shadowBlur = 7;
          ctx.fill();
          ctx.shadowBlur = 0;
        }
      }

      function resize() {
        const rect = hero.getBoundingClientRect();
        const ratio = window.devicePixelRatio || 1;
        width = Math.max(320, Math.floor(rect.width));
        height = Math.max(360, Math.floor(rect.height));
        canvas.width = Math.floor(width * ratio);
        canvas.height = Math.floor(height * ratio);
        canvas.style.width = width + 'px';
        canvas.style.height = height + 'px';
        ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
        const targetCount = Math.min(72, Math.max(30, Math.floor((width * height) / 23000)));
        nodes = Array.from({ length: targetCount }, () => new SubpageNode(true));
      }

      function drawConnections() {
        const maxDist = 132;
        for (let i = 0; i < nodes.length; i++) {
          for (let j = i + 1; j < nodes.length; j++) {
            const dx = nodes[i].x - nodes[j].x;
            const dy = nodes[i].y - nodes[j].y;
            const dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < maxDist) {
              const alpha = (1 - dist / maxDist) * 0.18;
              ctx.beginPath();
              ctx.moveTo(nodes[i].x, nodes[i].y);
              ctx.lineTo(nodes[j].x, nodes[j].y);
              ctx.strokeStyle = nodes[i].gold && nodes[j].gold
                ? `rgba(240,180,41,${alpha})`
                : `rgba(0,212,255,${alpha})`;
              ctx.lineWidth = 0.55;
              ctx.stroke();
            }
          }
        }
      }

      function frame() {
        if (document.hidden || !hero.closest('.page.active')) {
          rafId = null;
          return;
        }

        ctx.clearRect(0, 0, width, height);
        drawConnections();
        nodes.forEach(node => {
          node.update();
          node.draw();
        });
        rafId = requestAnimationFrame(frame);
      }

      function start() {
        if (rafId !== null) return;
        if (!width || !height) resize();
        rafId = requestAnimationFrame(frame);
      }

      function stop() {
        if (rafId === null) return;
        cancelAnimationFrame(rafId);
        rafId = null;
      }

      hero.addEventListener('mousemove', event => {
        const rect = hero.getBoundingClientRect();
        mouse.x = event.clientX - rect.left;
        mouse.y = event.clientY - rect.top;
      });
      hero.addEventListener('mouseleave', () => {
        mouse.x = -999;
        mouse.y = -999;
      });

      resize();
      window.addEventListener('resize', resize);
      subpageHeroScenes.push({ hero, start, stop });
    });

    updateSubpageHeroAnimations();
    document.addEventListener('visibilitychange', updateSubpageHeroAnimations);
  }

  function updateSubpageHeroAnimations() {
    subpageHeroScenes.forEach(scene => {
      if (!document.hidden && scene.hero.closest('.page.active')) {
        scene.start();
      } else {
        scene.stop();
      }
    });
  }

  async function loadJson(path) {
    const response = await fetch(path, { cache: 'no-store' });
    if (!response.ok) throw new Error('Falha ao carregar ' + path);
    return response.json();
  }

  // changeClass, escapeHtml, safeClass, safeUrl, safePercent, safeCssColor e
  // safeGradient agora vivem em ./modules/sanitize.js (funções puras, testadas
  // com Vitest). Ver doc/03-FRONTEND-STACK-CANONICA.md.

  function renderContentLink(item, fallbackLabel) {
    const href = safeUrl(item.url);
    if (!href) return '';
    const label = item.link_label || fallbackLabel;
    return `<a class="content-link" href="${escapeHtml(href)}" target="_blank" rel="noopener noreferrer">${escapeHtml(label)} →</a>`;
  }

  function renderTicker(items) {
    const ticker = document.getElementById('ticker');
    if (!ticker || !items || !items.length) return;
    const doubled = items.concat(items);
    ticker.innerHTML = doubled.map(item => `
      <div class="ticker-item">
        <span class="sym">${escapeHtml(item.symbol)}</span>
        <span class="val">${escapeHtml(item.value)}</span>
        <span class="${changeClass(item.direction)}">${escapeHtml(item.change)}</span>
      </div>
    `).join('');
  }

  function renderMarket(data) {
    const rows = document.getElementById('marketRows');
    if (!rows) return;

    if (!data.assets || !data.assets.length) {
      rows.innerHTML = '<tr><td colspan="5" class="loading-row">Execute scripts/update_market.py para gerar cotacoes reais.</td></tr>';
    } else {
      rows.innerHTML = data.assets.map(asset => {
        const arrow = asset.direction === 'up' ? '▲ ' : asset.direction === 'down' ? '▼ ' : '';
        const color = asset.direction === 'up' ? 'var(--green)' : asset.direction === 'down' ? 'var(--red)' : 'var(--text3)';
        return `
          <tr>
            <td class="asset-name">${escapeHtml(asset.icon)} ${escapeHtml(asset.name)}</td>
            <td>${escapeHtml(asset.quote)}</td>
            <td style="color:${color}">${arrow}${escapeHtml(asset.change)}</td>
            <td><span class="rec ${safeClass(asset.recommendation_class, 'rec-watch')}">${escapeHtml(asset.recommendation)}</span></td>
            <td>${escapeHtml(asset.reason)}</td>
          </tr>
        `;
      }).join('');
    }

    const mood = data.market_mood || {};
    document.getElementById('marketMood').innerHTML = `<strong>Humor de mercado: ${escapeHtml(mood.label || 'Atualizando')}.</strong> ${escapeHtml(mood.summary || '')}`;
    document.getElementById('marketDisclaimer').textContent = data.disclaimer || 'Conteúdo educacional.';
    document.getElementById('marketUpdated').textContent = data.generated_at
      ? `Atualizado em ${new Date(data.generated_at).toLocaleString('pt-BR')}. ${data.source_note || ''}`
      : data.source_note || '';
    renderTicker(data.ticker);
  }

  function renderTips(data) {
    const grid = document.getElementById('tipsGrid');
    if (!grid || !data.items || !data.items.length) return;
    grid.innerHTML = data.items.map(item => {
      const chart = item.chart ? `
        <div>
          <div style="background:var(--bg3);border-radius:12px;padding:24px;">
            <p style="font-size:0.8rem;color:var(--text3);margin-bottom:12px;text-transform:uppercase;letter-spacing:.08em;">Distribuicao sugerida</p>
            <div style="display:flex;flex-direction:column;gap:10px;">
              ${item.chart.map(bar => `
                <div>
                  <div style="display:flex;justify-content:space-between;font-size:0.85rem;margin-bottom:5px;"><span>${escapeHtml(bar.label)}</span><span style="color:${safeCssColor(bar.color)}">${safePercent(bar.value)}%</span></div>
                  <div style="height:8px;background:var(--surface2);border-radius:4px;"><div style="width:${safePercent(bar.value)}%;height:100%;background:${safeCssColor(bar.color)};border-radius:4px;"></div></div>
                </div>
              `).join('')}
            </div>
          </div>
        </div>
      ` : '';
      return `
        <div class="insight-card ${item.featured ? 'featured' : ''}">
          <div>
            <span class="insight-tag ${safeClass(item.tag_class, 'tag-blue')}">${escapeHtml(item.tag)}</span>
            <h3>${escapeHtml(item.title)}</h3>
            <p>${escapeHtml(item.body)}</p>
            <div class="insight-meta">${(item.meta || []).map(meta => `<span>${escapeHtml(meta)}</span>`).join('')}</div>
            ${renderContentLink(item, 'Ler mais')}
          </div>
          ${chart}
        </div>
      `;
    }).join('');
  }

  function renderRecipes(data) {
    const grid = document.getElementById('recipeGrid');
    if (!grid || !data.items || !data.items.length) return;
    grid.innerHTML = data.items.map(item => `
      <div class="recipe-card">
        <div class="recipe-thumb" style="background:${safeGradient(item.gradient)}">${escapeHtml(item.emoji)}</div>
        <div class="recipe-body">
          <h4>${escapeHtml(item.title)}</h4>
          <p>${escapeHtml(item.body)}</p>
          <div class="recipe-meta">${(item.meta || []).map(meta => `<span>${escapeHtml(meta)}</span>`).join('')}</div>
          ${renderContentLink(item, 'Ver receita')}
        </div>
      </div>
    `).join('');
  }

  function observeDynamicCards() {
    document.querySelectorAll('.card, .insight-card, .recipe-card, .pillar, .audience-card, .product-card').forEach(el => {
      el.style.opacity = '0';
      el.style.transform = 'translateY(16px)';
      el.style.transition = 'opacity .5s ease, transform .5s ease, border-color .25s';
      observer.observe(el);
    });
  }

  async function loadSiteData() {
    try {
      renderMarket(await loadJson('data/market.json'));
    } catch (error) {
      document.getElementById('marketUpdated').textContent = 'Nao foi possivel carregar data/market.json.';
    }

    try {
      renderTips(await loadJson('data/economy-tips.json'));
    } catch (error) {}

    try {
      renderRecipes(await loadJson('data/recipes.json'));
    } catch (error) {}

    observeDynamicCards();
  }

  // Radar (highlights) e Novidades (news): API com fallback para JSON estático.
  async function loadHomeContent() {
    const radarGrid = document.getElementById('radarGrid');
    const radarUpdated = document.getElementById('radarUpdated');
    if (radarGrid) {
      try {
        const { items, source } = await fetchWithFallback(
          apiConfig.highlightsApiUrl,
          'data/home-highlights.json',
        );
        radarGrid.innerHTML = buildHighlightsHtml(items);
        if (radarUpdated) {
          radarUpdated.textContent = source === 'api'
            ? 'Fonte: API TRCon'
            : 'Fonte: conteúdo publicado';
        }
        observeDynamicCards();
      } catch (error) {
        radarGrid.innerHTML = buildHighlightsHtml([]);
      }
    }

    const newsList = document.getElementById('newsList');
    if (newsList) {
      try {
        const { items } = await fetchWithFallback(
          apiConfig.newsApiUrl,
          'data/news-log.json',
        );
        newsList.innerHTML = buildNewsHtml(items.slice(0, 8));
      } catch (error) {
        newsList.innerHTML = buildNewsHtml([]);
      }
    }
  }

  // Fade-in on scroll (simple)
  const observer = new IntersectionObserver(entries => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        e.target.style.opacity = '1';
        e.target.style.transform = 'translateY(0)';
      }
    });
  }, { threshold: 0.1 });

  initHeroScene();
  initSubpageHeroScenes();
  setupNavigation();
  setupBetaWaitlist();
  loadSiteData();
  loadHomeContent();

