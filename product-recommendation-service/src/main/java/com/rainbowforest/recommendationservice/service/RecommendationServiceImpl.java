package com.rainbowforest.recommendationservice.service;

import com.rainbowforest.recommendationservice.model.Recommendation;
import com.rainbowforest.recommendationservice.repository.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Override
    public Recommendation saveRecommendation(Recommendation recommendation) {
        if (recommendation == null) {
            logger.warn("Attempted to save null recommendation");
            throw new IllegalArgumentException("Recommendation cannot be null");
        }
        Recommendation saved = recommendationRepository.save(recommendation);
        logger.info("Recommendation saved successfully with id: {}", saved.getId());
        return saved;
    }

    @Override
    public List<Recommendation> getAllRecommendationByProductName(String productName) {
        return recommendationRepository.findAllRatingByProductName(productName);
    }

    @Override
    public void deleteRecommendation(Long id) {
        if (id == null) {
            logger.warn("Attempted to delete recommendation with null id");
            throw new IllegalArgumentException("Recommendation id cannot be null");
        }
        recommendationRepository.deleteById(id);
        logger.info("Recommendation deleted with id: {}", id);
    }

	@Override
	public Recommendation getRecommendationById(Long recommendationId) {
		if (recommendationId == null) {
			logger.warn("Attempted to get recommendation with null id");
			throw new IllegalArgumentException("Recommendation id cannot be null");
		}
		return recommendationRepository.findById(recommendationId)
			.orElseThrow(() -> new RuntimeException("Recommendation not found with id: " + recommendationId));
	}
}
