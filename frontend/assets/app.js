import { apiConfig } from './modules/config.js?v=6872001';
import { buildLeadPayload, submitLead, mensagemDeErro } from './modules/lead-form.js?v=6872001';
import { buildHighlightsHtml, buildNewsHtml, fetchInstitutionalNews, fetchRadarHighlights } from './modules/content.js?v=6872001';
import { initChatWidget } from './modules/chat-widget.js?v=6872001';

const LEADS_API_URL = apiConfig.leadsApiUrl;

  const LEAD_CONTEXTS = {
    hub: {
      label: 'Produto em beta',
      title: 'Registre seu interesse no Sírius Hub',
      copy: 'Preencha seus dados para receber informações sobre a disponibilidade do beta.',
      note: 'O cadastro não garante acesso nem define condições comerciais. Esses detalhes serão informados antes de qualquer contratação.',
      leadType: 'PRODUTO',
      origem: 'site-trcongroup-hub',
      produtoLabel: 'Sírius Hub de Inteligência Financeira',
      submitLabel: 'Registrar interesse',
      successCopy:
        'Interesse registrado. Entraremos em contato quando houver informações sobre a disponibilidade do beta.',
      showUso: true,
    },
    agendamento: {
      label: 'Em desenvolvimento',
      title: 'Sírius Agendamento',
      copy: 'Cadastre seu interesse no assistente de agendamento autônomo. Avisaremos quando abrirmos testes guiados.',
      note: 'Produto em desenvolvimento — WhatsApp, agenda, confirmações e lembretes.',
      leadType: 'PRODUTO',
      origem: 'site-trcongroup-agendamento',
      produtoLabel: 'Sírius Agendamento',
      submitLabel: 'Quero ser avisado',
      successCopy:
        'Interesse registrado. Avisaremos quando o Sírius Agendamento estiver disponível para testes.',
      showUso: false,
    },
    marketing: {
      label: 'Em desenvolvimento',
      title: 'Sírius Marketing',
      copy: 'Cadastre seu interesse na plataforma de conteúdo, aprovação e publicação. Avisaremos no acesso antecipado.',
      note: 'Produto em desenvolvimento — editorial, calendário, site e redes sociais.',
      leadType: 'PRODUTO',
      origem: 'site-trcongroup-marketing',
      produtoLabel: 'Sírius Marketing',
      submitLabel: 'Quero ser avisado',
      successCopy:
        'Interesse registrado. Avisaremos quando o Sírius Marketing estiver disponível para testes.',
      showUso: false,
    },
    servicos: {
      label: 'Serviços',
      title: 'Fale com um especialista',
      copy: 'Conte o que você precisa — projeto sob demanda, customização ou alocação de time — e retornamos com o melhor caminho.',
      note: 'Atendimento comercial da TRCONGROUP. Sem compromisso de waitlist de produto.',
      leadType: 'ALOCACAO_MAO_DE_OBRA',
      origem: 'site-trcongroup-servicos',
      produtoLabel: '',
      submitLabel: 'Enviar mensagem',
      successCopy: 'Mensagem recebida. Em breve entraremos em contato.',
      showUso: false,
    },
    ia: {
      label: 'Diagnóstico de IA',
      title: 'Onde a IA pode gerar valor?',
      copy: 'Conte o processo, gargalo ou ideia que você quer avaliar. Vamos analisar o contexto e indicar um próximo passo viável.',
      note: 'Conversa inicial com foco em aplicação prática, dados necessários, integrações e limites da solução.',
      leadType: 'DESENVOLVIMENTO_SOB_DEMANDA',
      origem: 'site-trcongroup-diagnostico-ia',
      produtoLabel: '',
      submitLabel: 'Solicitar diagnóstico',
      successCopy: 'Solicitação recebida. Em breve entraremos em contato para entender o contexto.',
      showUso: false,
    },
    mvp: {
      label: 'MVP ou produto sob demanda',
      title: 'Fale sobre o produto que precisa construir',
      copy: 'Conte a oportunidade, operação ou ideia que precisa virar software. Vamos entender o contexto e estruturar o próximo passo.',
      note: 'A conversa inicial orienta escopo, riscos, marcos e o formato de contratação mais adequado.',
      leadType: 'DESENVOLVIMENTO_SOB_DEMANDA',
      origem: 'site-trcongroup-oferta-mvp',
      produtoLabel: '',
      submitLabel: 'Falar sobre um projeto',
      successCopy: 'Mensagem recebida. Entraremos em contato para entender o contexto do projeto.',
      showUso: false,
    },
    modernizacao: {
      label: 'Modernização e customização',
      title: 'Evolua seu sistema com segurança',
      copy: 'Descreva o sistema, a limitação atual e a mudança que sua operação precisa.',
      note: 'A proposta é preparada depois de entendermos ambiente, prioridades, integrações e riscos de transição.',
      leadType: 'CUSTOMIZACAO',
      origem: 'site-trcongroup-oferta-modernizacao',
      produtoLabel: '',
      submitLabel: 'Receber uma proposta',
      successCopy: 'Mensagem recebida. Entraremos em contato para entender a evolução necessária.',
      showUso: false,
    },
    outsourcing: {
      label: 'Outsourcing e squads',
      title: 'Monte a capacidade que seu time precisa',
      copy: 'Conte a frente de trabalho, as competências necessárias e como o novo time deve se integrar à operação.',
      note: 'Perfil, composição, responsabilidades, período e capacidade são definidos na proposta.',
      leadType: 'ALOCACAO_MAO_DE_OBRA',
      origem: 'site-trcongroup-oferta-outsourcing',
      produtoLabel: '',
      submitLabel: 'Montar meu time',
      successCopy:
        'Mensagem recebida. Entraremos em contato para entender a composição necessária.',
      showUso: false,
    },
    default: {
      label: 'Contato',
      title: 'Vamos conversar',
      copy: 'Preencha o formulário. Usamos seus dados apenas para retornar o contato comercial.',
      note: 'Escolha o tipo de interesse e descreva brevemente o que você precisa.',
      leadType: 'PRODUTO',
      origem: 'site-trcongroup',
      produtoLabel: '',
      submitLabel: 'Enviar mensagem',
      successCopy: 'Seus dados foram enviados. Logo entraremos em contato.',
      showUso: false,
    },
  };

  let contatoContextKey = 'default';

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
  function closeMobile() { document.getElementById('mobileNav').classList.remove('open'); }
  function toggleMobile() { document.getElementById('mobileNav').classList.toggle('open'); }

  function applyContatoContext(productKey, leadType) {
    const key = LEAD_CONTEXTS[productKey] ? productKey : 'default';
    const ctx = LEAD_CONTEXTS[key];
    contatoContextKey = key;

    const setText = (id, value) => {
      const el = document.getElementById(id);
      if (el) el.textContent = value;
    };

    setText('contatoContextLabel', ctx.label);
    setText('contatoContextTitle', ctx.title);
    setText('contatoContextCopy', ctx.copy);
    setText('contatoContextNote', ctx.note);
    setText('contatoSuccessCopy', ctx.successCopy);

    const submitBtn = document.getElementById('contatoSubmitBtn');
    if (submitBtn) submitBtn.textContent = ctx.submitLabel;

    const usoField = document.getElementById('contatoUsoField');
    if (usoField) usoField.hidden = !ctx.showUso;

    const tipo = leadType || ctx.leadType;
    preselectLeadType(tipo);
  }

  // Pré-seleciona o tipo de interesse no formulário quando o usuário chega por
  // um CTA que carrega data-lead-type (ex.: "Montar um time" -> ALOCACAO...).
  function preselectLeadType(tipo) {
    if (!tipo) return;
    const select = document.querySelector('#contatoLeadForm select[name="tipoInteresse"]');
    if (select && [...select.options].some(o => o.value === tipo)) {
      select.value = tipo;
    }
  }

  function setupNavigation() {
    document.querySelectorAll('[data-page-link]').forEach(link => {
      link.addEventListener('click', event => {
        event.preventDefault();
        const pageId = link.dataset.pageLink;
        showPage(pageId);
        if (pageId === 'contato') {
          applyContatoContext(link.dataset.product, link.dataset.leadType);
        } else if (link.dataset.leadType) {
          preselectLeadType(link.dataset.leadType);
        }
        if (link.hasAttribute('data-close-mobile')) closeMobile();
      });
    });

    document.querySelector('[data-mobile-open]')?.addEventListener('click', toggleMobile);
    document.querySelector('[data-mobile-close]')?.addEventListener('click', closeMobile);
    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') closeMobile();
    });
    // Clique no backdrop (fora do painel) também fecha o menu.
    document.getElementById('mobileNav')?.addEventListener('click', event => {
      if (event.target.id === 'mobileNav') closeMobile();
    });
  }

  function setupContatoLeadForm() {
    const form = document.getElementById('contatoLeadForm');
    const success = document.getElementById('contatoLeadSuccess');
    if (!form || !success) return;

    const hideSuccess = () => {
      success.hidden = true;
    };

    form.addEventListener('input', hideSuccess);
    form.addEventListener('change', hideSuccess);

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      hideSuccess();
      const submitButton = form.querySelector('button[type="submit"]');
      const ctx = LEAD_CONTEXTS[contatoContextKey] || LEAD_CONTEXTS.default;
      const originalLabel = submitButton ? submitButton.textContent : ctx.submitLabel;
      const entries = Object.fromEntries(new FormData(form).entries());
      const payload = buildLeadPayload(entries, {
        origem: ctx.origem,
        produtoLabel: ctx.produtoLabel,
        defaultTipoInteresse: ctx.leadType,
      });

      if (submitButton) {
        submitButton.disabled = true;
        submitButton.textContent = 'Enviando...';
      }

      try {
        await submitLead(LEADS_API_URL, payload);
        form.reset();
        preselectLeadType(ctx.leadType);
        success.hidden = false;
        success.scrollIntoView({ behavior: 'smooth', block: 'center' });
      } catch (error) {
        // Degradação previsível: o formulário mostra erro claro, a página não quebra.
        alert(mensagemDeErro(error));
      } finally {
        if (submitButton) {
          submitButton.disabled = false;
          submitButton.textContent = originalLabel || ctx.submitLabel;
        }
      }
    });
  }

  // Lightbox das screenshots do Hub (#page-hub) — mesmo idioma de fechar do
  // menu mobile (clique fora, tecla Escape), sem depender dele.
  function setupHubLightbox() {
    const lightbox = document.getElementById('hubLightbox');
    const lightboxImg = document.getElementById('hubLightboxImg');
    if (!lightbox || !lightboxImg) return;

    const openLightbox = (src, alt) => {
      lightboxImg.src = src;
      lightboxImg.alt = alt || '';
      lightbox.classList.add('open');
    };
    const closeLightbox = () => {
      lightbox.classList.remove('open');
      lightboxImg.src = '';
    };

    document.querySelectorAll('.hub-shot img').forEach(img => {
      img.addEventListener('click', () => openLightbox(img.src, img.alt));
    });

    lightbox.addEventListener('click', event => {
      if (event.target === lightbox) closeLightbox();
    });
    document.getElementById('hubLightboxClose')?.addEventListener('click', closeLightbox);
    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') closeLightbox();
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
      el.style.setProperty('--node-left', Math.random() * 86 + 7 + '%');
      el.style.setProperty('--node-bottom', Math.random() * 28 + 12 + '%');
      el.style.setProperty('--node-duration', duration + 's');
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

  function setPublishedContentBlockVisible(blockId, visible) {
    const block = document.getElementById(blockId);
    if (!block) return;
    block.hidden = !visible;
  }

  function observeDynamicCards() {
    document.querySelectorAll('.card, .pillar, .audience-card, .product-card, .process-step, .offer-card, .help-card').forEach(el => {
      if (el.classList.contains('reveal')) return;
      el.classList.add('reveal');
      observer.observe(el);
    });
  }

  // Radar usa fallback estático. Novidades vêm somente da API institucional;
  // se ela estiver vazia ou indisponível, a seção fica oculta.
  async function loadPublishedContent() {
    const radarGrid = document.getElementById('radarGrid');
    const radarUpdated = document.getElementById('radarUpdated');
    const newsList = document.getElementById('newsList');

    if (newsList) {
      try {
        const items = await fetchInstitutionalNews(apiConfig.newsApiUrl);
        const visible = items.slice(0, 8);
        newsList.innerHTML = buildNewsHtml(visible);
        newsList.removeAttribute('aria-busy');
        setPublishedContentBlockVisible('block-news', visible.length > 0);
      } catch (error) {
        newsList.innerHTML = '';
        newsList.removeAttribute('aria-busy');
        setPublishedContentBlockVisible('block-news', false);
      }
    }

    if (radarGrid) {
      try {
        const { items, source } = await fetchRadarHighlights(
          apiConfig.highlightsApiUrl,
          'data/home-highlights.json',
        );
        radarGrid.innerHTML = buildHighlightsHtml(items);
        radarGrid.removeAttribute('aria-busy');
        setPublishedContentBlockVisible('block-radar', items.length > 0);
        if (radarUpdated) {
          radarUpdated.textContent = items.length
            ? (source === 'api' ? 'Fonte: API TRCONGROUP' : 'Fonte: conteúdo publicado')
            : '';
        }
        if (items.length) observeDynamicCards();
      } catch (error) {
        radarGrid.innerHTML = '';
        radarGrid.removeAttribute('aria-busy');
        setPublishedContentBlockVisible('block-radar', false);
      }
    }
  }

  // Fade-in on scroll (simple)
  const observer = new IntersectionObserver(entries => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        e.target.classList.add('is-visible');
        observer.unobserve(e.target);
      }
    });
  }, { threshold: 0.1 });

  initHeroScene();
  initSubpageHeroScenes();
  setupNavigation();
  setupContatoLeadForm();
  setupHubLightbox();
  applyContatoContext('default');
  observeDynamicCards();
  loadPublishedContent();
  initChatWidget({
    apiUrl: apiConfig.chatApiUrl,
    getPageId: () => document.querySelector('.page.active')?.id.replace('page-', '') || 'home',
    onContact: () => { showPage('contato'); applyContatoContext('servicos'); },
    onCareers: () => showPage('carreiras'),
  });

  // Deep link: abre direto a página indicada na URL (ex.: trcongroup.com.br/#hub),
  // para convites pessoais e campanhas que devem cair direto numa página específica
  // sem exigir navegação manual pelo menu. Não altera a navegação por clique
  // existente — só lê o hash uma vez, no carregamento da página.
  (function applyInitialHashRoute() {
    const requestedId = (window.location.hash || '').replace('#', '');
    const aliases = { clientes: 'como-ajudamos', conteudos: 'conteudo' };
    const id = aliases[requestedId] || requestedId;
    if (id && document.getElementById('page-' + id)) {
      showPage(id);
    }
  })();
