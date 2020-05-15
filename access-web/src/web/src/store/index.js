import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios'

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    users: [],
  },
  getters: {
    USERS: (state) => state.users,
  },
  mutations: {
    SET_USERS: (state, payload) => {
      state.users = payload;
    },
  },
  actions: {
    GET_USERS: async (context) => {
      let { data } = await axios.get('/api/users')
      context.commit('SET_USERS', data)
    },
    SAVE_USER: async (context, payload) => {
      let { data } = await axios.post('/api/user', payload)
      console.log('committed: ' + data)
      // context.commit('ADD_TODO',payload)
    },
  },
  modules: {
  },
});
