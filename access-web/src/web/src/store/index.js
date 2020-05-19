import Vue from 'vue';
import Vuex from 'vuex';
import { getField, updateField } from 'vuex-map-fields';
import axios from 'axios';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    roles: [],
    users: [],
    stage: 0,
    requestForm: {
      effectiveDate: new Date().toISOString().substr(0, 10),
      expiryDate: null,
      comments: null,
      attachments: [],
    },
  },
  getters: {
    getField,
    ROLES: (state) => state.roles,
    USERS: (state) => state.users,
    STAGE: (state) => state.stage,
    REQUESTFORM: (state) => state.requestForm,
  },
  mutations: {
    updateField,
    SET_ROLES: (state, payload) => {
      state.roles = payload;
    },
    SET_USERS: (state, payload) => {
      state.users = payload;
    },
    SET_STAGE: (state, payload) => {
      state.stage = payload;
    },
  },
  actions: {
    GET_ROLES: async (context) => {
      let { data } = await axios.get('/api/lookup/roles');
      context.commit('SET_ROLES', data);
    },
    GET_USERS: async (context) => {
      let { data } = await axios.get('/api/users');
      context.commit('SET_USERS', data);
    },
    SAVE_USER: async (context, payload) => {
      let { data } = await axios.post('/api/user', payload);
      console.log('committed: ' + data);
      // context.commit('ADD_TODO',payload)
    },
    SET_STAGE: (context, stage) => {
      context.commit('SET_STAGE', stage);
    }
  },
  modules: {
  },
});
