const MAX_HISTORY_MESSAGES = 12;

export function buildChatPayload(message, history = [], origem = 'site-trcon-chat') {
  const clean = String(message || '').trim();
  if (!clean) throw new Error('EMPTY_MESSAGE');
  return {
    message: clean.slice(0, 800),
    history: history.slice(-MAX_HISTORY_MESSAGES).map(({ role, content }) => ({
      role: role === 'assistant' ? 'assistant' : 'user',
      content: String(content || '').trim().slice(0, 800),
    })).filter(item => item.content),
    origem,
  };
}

export function parseChatResponse(value) {
  if (!value || typeof value.reply !== 'string' || !value.reply.trim()) throw new Error('INVALID_RESPONSE');
  return {
    reply: value.reply.trim(),
    disclaimer: typeof value.disclaimer === 'string' ? value.disclaimer : '',
    suggestContactForm: value.suggestContactForm === true,
    suggestCareersPage: value.suggestCareersPage === true,
  };
}

export function mensagemDeErroChat(status, code) {
  if (code === 'CHAT_RATE_LIMITED' || status === 429) return 'Muitas mensagens em pouco tempo. Aguarde um instante e tente novamente.';
  if (code === 'CHAT_BUDGET_EXCEEDED' || code === 'AI_PROVIDER_UNAVAILABLE' || status === 503) return 'O assistente está indisponível agora. Você ainda pode falar com nosso time pelo formulário.';
  return 'Não foi possível obter a resposta. Tente novamente ou fale com nosso time.';
}

export function initChatWidget({ apiUrl, getPageId, onContact, onCareers }) {
  const root = document.getElementById('aiChat');
  if (!root || !apiUrl) return;
  const toggle = document.getElementById('aiChatToggle');
  const panel = document.getElementById('aiChatPanel');
  const close = document.getElementById('aiChatClose');
  const consent = document.getElementById('aiChatConsent');
  const gate = document.getElementById('aiChatGate');
  const form = document.getElementById('aiChatForm');
  const input = document.getElementById('aiChatInput');
  const messages = document.getElementById('aiChatMessages');
  const submit = form?.querySelector('button[type="submit"]');
  const history = [];

  const setOpen = open => {
    panel.hidden = !open;
    toggle.setAttribute('aria-expanded', String(open));
    if (open && consent.hidden) input.focus();
  };
  toggle.addEventListener('click', () => setOpen(panel.hidden));
  close.addEventListener('click', () => setOpen(false));
  consent.addEventListener('click', () => { gate.hidden = true; form.hidden = false; messages.hidden = false; input.focus(); });

  const addMessage = (role, text) => {
    const item = document.createElement('div');
    item.className = `ai-chat-message ${role}`;
    item.textContent = text;
    messages.append(item);
    messages.scrollTop = messages.scrollHeight;
  };
  const addCta = (label, action) => {
    const button = document.createElement('button');
    button.type = 'button'; button.className = 'ai-chat-cta'; button.textContent = label;
    button.addEventListener('click', action); messages.append(button);
  };

  form.addEventListener('submit', async event => {
    event.preventDefault();
    const text = input.value.trim();
    if (!text) return;
    addMessage('user', text); input.value = ''; submit.disabled = true;
    try {
      const payload = buildChatPayload(text, history, `site-trcon-chat-${getPageId()}`);
      const response = await fetch(apiUrl, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
      const json = await response.json().catch(() => ({}));
      if (!response.ok) throw Object.assign(new Error('CHAT_ERROR'), { status: response.status, code: json.code });
      const answer = parseChatResponse(json);
      addMessage('assistant', answer.reply);
      history.push({ role: 'user', content: text }, { role: 'assistant', content: answer.reply });
      if (answer.suggestContactForm) addCta('Falar com a TRCONGROUP →', onContact);
      if (answer.suggestCareersPage) addCta('Ver Trabalhe Conosco →', onCareers);
    } catch (error) {
      addMessage('error', mensagemDeErroChat(error.status, error.code));
      addCta('Usar formulário de contato →', onContact);
    } finally { submit.disabled = false; input.focus(); }
  });
}
