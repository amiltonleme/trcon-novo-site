package br.com.trcon.site.chat.dto;

import java.util.List;

public record ChatResponse(
        String reply, List<String> sourceIds, String disclaimer,
        boolean outOfScope, boolean knowledgeMissing,
        boolean suggestContactForm, boolean suggestCareersPage) {}
