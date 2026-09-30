package br.com.trcon.site.chat.service;

import br.com.trcon.site.chat.dto.ChatRequest;
import br.com.trcon.site.chat.dto.ChatResponse;

public interface ChatService { ChatResponse reply(ChatRequest request, String clientKey); }
