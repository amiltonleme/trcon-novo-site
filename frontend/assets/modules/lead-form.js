// Lógica de captação de lead comercial.
// buildLeadPayload é pura (testável sem DOM/rede); submitLead recebe o fetch por
// injeção para poder ser testado sem rede. Ver doc/03-FRONTEND-STACK-CANONICA.md
// e doc/06-BACKEND-MINIMO-ESPECIFICACAO.md (contrato do endpoint de leads).

// Espelha o enum LeadType do backend.
export const LEAD_TYPES = [
  'PRODUTO',
  'DESENVOLVIMENTO_SOB_DEMANDA',
  'CUSTOMIZACAO',
  'ALOCACAO_MAO_DE_OBRA',
];

const DEFAULT_LEAD_TYPE = 'PRODUTO';

function normalizarTipo(candidato, fallback) {
  if (LEAD_TYPES.includes(candidato)) return candidato;
  if (LEAD_TYPES.includes(fallback)) return fallback;
  return DEFAULT_LEAD_TYPE;
}

// Monta o payload no contrato do backend (POST /api/v1/site/leads) a partir das
// entradas do formulário. Função pura.
export function buildLeadPayload(entries = {}, options = {}) {
  const tipoInteresse = normalizarTipo(entries.tipoInteresse, options.defaultTipoInteresse);

  const partesMensagem = [];
  if (entries.mensagem && String(entries.mensagem).trim()) {
    partesMensagem.push(String(entries.mensagem).trim());
  }
  // "uso pretendido" é contexto específico do produto; se veio, preserva dentro
  // da mensagem (o backend não tem campo próprio para isso).
  if (entries.uso && String(entries.uso).trim()) {
    partesMensagem.push(`Uso pretendido: ${String(entries.uso).trim()}`);
  }
  const mensagem = partesMensagem.length ? partesMensagem.join('\n') : undefined;

  return {
    nome: String(entries.nome || '').trim(),
    email: String(entries.email || '').trim(),
    telefone: String(entries.telefone || '').trim(),
    tipoInteresse,
    mensagem,
    origem: options.origem || 'site-trcon',
    consentimentoLgpd: true,
  };
}

// Envia o lead ao backend. Recebe fetch por injeção (deps.fetch) para teste.
// Lança Error com .status e .code em caso de resposta não-2xx; propaga TypeError
// (rede/backend fora do ar) para o chamador tratar o fallback.
export async function submitLead(url, payload, deps = {}) {
  const fetchImpl = deps.fetch || (typeof fetch !== 'undefined' ? fetch : null);
  if (!fetchImpl) throw new Error('fetch indisponível neste ambiente.');

  const response = await fetchImpl(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const body = await response.json().catch(() => null);
    const error = new Error(
      body?.message || body?.detail || 'Não foi possível enviar o cadastro agora.',
    );
    error.status = response.status;
    error.code = body?.code || null;
    throw error;
  }

  return response.json().catch(() => ({}));
}

// Traduz uma falha de envio em mensagem amigável para o usuário, sem quebrar a
// página (política de degradação de doc/07-MIGRACAO-PARALELA.md).
export function mensagemDeErro(error) {
  if (error instanceof TypeError) {
    return 'Não foi possível conectar ao servidor agora. Tente novamente em instantes.';
  }
  if (error?.code === 'LEAD_DUPLICADO') {
    return 'Já recebemos um cadastro com este e-mail. Em breve entramos em contato.';
  }
  return error?.message || 'Não foi possível enviar o cadastro agora.';
}
