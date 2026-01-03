import { defineStore } from 'pinia';
import { coachService } from '../services/coachService';

export const useCoachStore = defineStore('coach', {
  state: () => ({
    coaches: [],
    userCoaches: [],
    loading: false,
    error: null
  }),

  getters: {
    allCoaches: (state) => state.coaches,
    selectedCoaches: (state) => state.userCoaches
  },

  actions: {
    // 获取单个教练的评分
    async getCoachRating(coachId) {
      return await coachService.getCoachRating(coachId);
    },

    // 为教练列表添加评分
    async addRatingsToCoaches(coaches) {
      const coachesWithRatings = await Promise.all(
        coaches.map(async (coach) => {
          const rating = await this.getCoachRating(coach.coachId);
          return { ...coach, rating: rating || 0.0 };
        })
      );
      return coachesWithRatings;
    },

    async fetchCoaches() {
      try {
        this.loading = true;
        this.error = null;
        const coaches = await coachService.getValidCoaches();
        const coachesWithRatings = await this.addRatingsToCoaches(coaches);
        this.coaches = coachesWithRatings;
        return coachesWithRatings;
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async fetchUserCoaches(userId) {
      try {
        this.loading = true;
        this.error = null;
        const coaches = await coachService.getUserCoaches(userId);
        const coachesWithRatings = await this.addRatingsToCoaches(coaches);
        this.userCoaches = coachesWithRatings;
        return coachesWithRatings;
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async selectCoach(userId, coachId) {
      try {
        this.loading = true;
        this.error = null;
        const result = await coachService.selectCoach(userId, coachId);
        await this.fetchUserCoaches(userId);
        return result;
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async cancelCoach(userId, coachId) {
      try {
        this.loading = true;
        this.error = null;
        const result = await coachService.cancelCoach(userId, coachId);
        await this.fetchUserCoaches(userId);
        return result;
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    clearError() {
      this.error = null;
    }
  }
});
