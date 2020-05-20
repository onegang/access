import Vue from 'vue';
import Vuex from 'vuex';
import { getField, updateField } from 'vuex-map-fields';
import axios from 'axios';

Vue.use(Vuex);

const store = new Vuex.Store({
  state: {
    roles: [],
    users: [],
    stage: 0,
    error: null,
    requestForm: {
      effectiveDate: new Date().toISOString().substr(0, 10),
      expiryDate: null,
      comments: null,
      attachments: [],
    },
  },
  getters: {
    getField,
    ERROR: (state) => state.error,
    ROLES: (state) => state.roles,
    USERS: (state) => state.users,
    STAGE: (state) => state.stage,
    REQUESTFORM: (state) => state.requestForm,
  },
  mutations: {
    updateField,
    SET_ROLES: (state, roles) => {
      state.roles = roles;
    },
    SET_USERS: (state, users) => {
      state.users = users;
    },
    SET_STAGE: (state, stage) => {
      state.stage = stage;
    },
    SET_ERROR: (state, error) => {
      state.error = error;
    },
  },
  actions: {
    CLEAR_ERROR: (context) => {
      context.commit('SET_ERROR', null);
    },
    GET_ROLES: async (context) => {
      const { data } = await axios.get('/api/lookup/roles');
      context.commit('SET_ROLES', data);
    },
    GET_USERS: async (context) => {
      const { data } = await axios.get('/api/users');
      context.commit('SET_USERS', data);
    },
    SET_STAGE: (context, stage) => {
      context.commit('SET_STAGE', stage);

      if(stage===2) {
        axios.post('/api/users/changes', context.state.users.filter(user => user.selected)).
          then((response) => {
            const changes = response.data;
            console.log(changes);
          });
      }
    }
  },
  modules: {
  },
});

//common REST handling
axios.interceptors.response.use((response) => {
  return response;
}, (error) => {
  // handle error
  const {data} = error.response;
  if(data.status===500)
    store.commit('SET_ERROR', data.message);
  else
    store.commit('SET_ERROR', data.error);
  return Promise.reject(error);
});

export default store;