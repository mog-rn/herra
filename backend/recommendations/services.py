import google.generativeai as genai
from django.conf import settings
from django.core.cache import cache
from .models import Recommendation, RecommendationType

class GeminiService:
    def __init__(self):
        genai.configure(api_key=settings.GEMINI_API_KEY)
        self.model = genai.GenerativeModel('gemini-pro')

    def generate_recommendations(self, user, cycle_phase, symptoms=None):
        """
        Generate personalized recommendations based on cycle phase and symptoms
        """
        recommendations = {}
        
        for rec_type in RecommendationType.choices:
            cache_key = f"recommendation_{user.id}_{cycle_phase}_{rec_type[0]}"
            cached_rec = cache.get(cache_key)
            
            if cached_rec:
                recommendations[rec_type[0]] = cached_rec
                continue

            prompt = self._create_prompt(cycle_phase, rec_type[0], symptoms)
            response = self._get_gemini_response(prompt)
            
            if response:
                # Store recommendation
                recommendation = Recommendation.objects.create(
                    user=user,
                    cycle_phase=cycle_phase,
                    type=rec_type[0],
                    content=response,
                    gemini_prompt=prompt
                )
                recommendations[rec_type[0]] = response
                
                # Cache for 12 hours
                cache.set(cache_key, response, 60 * 60 * 12)

        return recommendations

    def _create_prompt(self, cycle_phase, rec_type, symptoms=None):
        base_prompts = {
            'nutrition': """
                Provide 3 specific nutrition recommendations for someone in their {phase} phase.
                Consider these symptoms: {symptoms}.
                Format as:
                1. [Recommendation]
                2. [Recommendation]
                3. [Recommendation]
                Keep it concise and practical.
            """,
            'exercise': """
                Suggest 3 exercise activities suitable for the {phase} phase.
                Consider these symptoms: {symptoms}.
                Include intensity levels and duration.
                Format as:
                1. [Activity] - [Intensity] - [Duration]
                2. [Activity] - [Intensity] - [Duration]
                3. [Activity] - [Intensity] - [Duration]
            """,
            'wellness': """
                Provide 3 self-care recommendations for the {phase} phase.
                Consider these symptoms: {symptoms}.
                Include practical tips that can be done at home.
                Format as:
                1. [Recommendation]
                2. [Recommendation]
                3. [Recommendation]
            """,
            'productivity': """
                Suggest 3 productivity strategies during the {phase} phase.
                Consider these symptoms: {symptoms}.
                Include practical workplace or study tips.
                Format as:
                1. [Strategy]
                2. [Strategy]
                3. [Strategy]
            """,
            'sleep': """
                Provide 3 sleep optimization tips for the {phase} phase.
                Consider these symptoms: {symptoms}.
                Include practical bedtime routine suggestions.
                Format as:
                1. [Tip]
                2. [Tip]
                3. [Tip]
            """
        }

        symptoms_text = "No specific symptoms reported"
        if symptoms:
            symptoms_text = ", ".join([f"{k}: {v}" for k, v in symptoms.items()])

        return base_prompts[rec_type].format(
            phase=cycle_phase,
            symptoms=symptoms_text
        )

    def _get_gemini_response(self, prompt):
        try:
            response = self.model.generate_content(prompt)
            return response.text
        except Exception as e:
            print(f"Gemini API error: {str(e)}")
            return None
            