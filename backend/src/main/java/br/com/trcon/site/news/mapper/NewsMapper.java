package br.com.trcon.site.news.mapper;

import br.com.trcon.site.news.domain.NewsItem;
import br.com.trcon.site.news.dto.response.NewsItemResponse;
import br.com.trcon.site.news.dto.response.NewsListResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    NewsItemResponse toResponse(NewsItem newsItem);

    List<NewsItemResponse> toResponseList(List<NewsItem> newsItems);

    default NewsListResponse toListResponse(List<NewsItem> newsItems) {
        return NewsListResponse.of(toResponseList(newsItems));
    }
}
