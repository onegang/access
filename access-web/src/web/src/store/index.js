import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios'

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    users: [],
    workflow: {
      stage: 0,
      comments: null,
    },
  },
  getters: {
    USERS: (state) => state.users,
    WORKFLOW: (state) => state.workflow,
  },
  mutations: {
    SET_USERS: (state, payload) => {
      state.users = payload;
    },
    SET_WF_STAGE: (state, payload) => {
      state.workflow.stage = payload;
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
    SET_STAGE: (context, stage) => {
      context.commit('SET_WF_STAGE', stage)
    }
  },
  modules: {
  },
});
