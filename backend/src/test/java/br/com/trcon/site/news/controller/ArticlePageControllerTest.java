package br.com.trcon.site.news.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.trcon.site.news.service.ArticlePageService;
import br.com.trcon.site.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ArticlePageControllerTest {

    @Mock
    private ArticlePageService articlePageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ArticlePageController(articlePageService)).build();
    }

    @Test
    void deveServirHtmlCompletoPorSlug() throws Exception {
        when(articlePageService.renderHtml(eq("artigo-ssr")))
                .thenReturn("<!DOCTYPE html><html><head><title>Artigo SSR</title>"
                        + "<script type=\"application/ld+json\">{}</script></head>"
                        + "<body data-article-ssr=\"true\">corpo</body></html>");

        mockMvc.perform(get("/novidades/artigo-ssr").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("application/ld+json")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-article-ssr")));
    }

    @Test
    void deveResponder404HtmlQuandoSlugInexistente() throws Exception {
        when(articlePageService.renderHtml(eq("nao-existe")))
                .thenThrow(new ResourceNotFoundException("Artigo não encontrado: nao-existe"));
        when(articlePageService.notFoundHtml())
                .thenReturn("<html><meta name=\"robots\" content=\"noindex\" />"
                        + "<p>Não foi possível carregar este artigo.</p></html>");

        mockMvc.perform(get("/novidades/nao-existe").accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("noindex")));
    }
}
