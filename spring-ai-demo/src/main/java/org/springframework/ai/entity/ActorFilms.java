package org.springframework.ai.entity;

import java.util.List;

/**
 * @author wrp
 * @since 2025年04月22日 13:47
 **/
public record ActorFilms(String actor, List<String> movies) {
}
