import { describe, expect, it } from 'vitest';
import { buildChatPayload, mensagemDeErroChat, parseChatResponse } from '../../assets/modules/chat-widget.js';

describe('buildChatPayload', () => {
  it('limita o histórico às 12 mensagens mais recentes', () => {
    const history = Array.from({ length: 15 }, (_, index) => ({ role: 'user', content: `m${index}` }));
    const payload = buildChatPayload(' Olá ', history, 'site-trcon-chat-home');
    expect(payload.message).toBe('Olá');
    expect(payload.history).toHaveLength(12);
    expect(payload.history[0].content).toBe('m3');
  });
  it('rejeita mensagem vazia', () => expect(() => buildChatPayload(' ')).toThrow('EMPTY_MESSAGE'));
});

describe('parseChatResponse', () => {
  it('aplica defaults seguros às flags ausentes', () => {
    expect(parseChatResponse({ reply: 'Resposta' })).toEqual({
      reply: 'Resposta', disclaimer: '', suggestContactForm: false, suggestCareersPage: false,
    });
  });
  it('rejeita resposta sem texto', () => expect(() => parseChatResponse({})).toThrow('INVALID_RESPONSE'));
});

describe('mensagemDeErroChat', () => {
  it('mapeia rate limit', () => expect(mensagemDeErroChat(429, 'CHAT_RATE_LIMITED')).toContain('Muitas mensagens'));
  it('mapeia indisponibilidade', () => expect(mensagemDeErroChat(503, 'AI_PROVIDER_UNAVAILABLE')).toContain('indisponível'));
  it('usa fallback', () => expect(mensagemDeErroChat(500, 'X')).toContain('Não foi possível'));
});
