import { parseArticleSlug, fetchArticleBySlug, renderArticlePage } from './modules/article.js';

async function bootArticlePage() {
  const slug = parseArticleSlug(window.location.pathname);
  const root = document.getElementById('articleRoot');
  const loading = document.getElementById('articleLoading');
  const error = document.getElementById('articleError');

  if (!slug || !root) {
    if (error) {
      error.hidden = false;
      error.textContent = 'Artigo não encontrado.';
    }
    if (loading) loading.hidden = true;
    return;
  }

  try {
    const article = await fetchArticleBySlug(slug);
    renderArticlePage(article, root, { siteBase: window.TRCON_SITE_BASE_URL || 'https://trcongroup.com.br' });
    if (loading) loading.hidden = true;
    root.hidden = false;
  } catch (err) {
    if (loading) loading.hidden = true;
    if (error) {
      error.hidden = false;
      error.textContent = 'Não foi possível carregar este artigo.';
    }
  }
}

bootArticlePage();
