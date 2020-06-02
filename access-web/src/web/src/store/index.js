import Vue from 'vue';
import Vuex from 'vuex';
import { getField, updateField } from 'vuex-map-fields';
import axios from 'axios';

Vue.use(Vuex);

const defaultForm = {
      effectiveDate: new Date().toISOString().substr(0, 10),
      expiryDate: null,
      purpose: null,
      comments: null,
      manual: null,
      supporters: [],
      approvers: [],
      attachments: [],
    };

const store = new Vuex.Store({
  state: {
    roles: [],
    users: [],
    stage: 0,
    error: null,
    requestForm: Object.assign({}, defaultForm),
    changes: null,
    requests: null,
    requestDetail: null,
  },
  getters: {
    getField,
    ERROR: (state) => state.error,
    ROLES: (state) => state.roles,
    USERS: (state) => state.users,
    STAGE: (state) => state.stage,
    REQUESTFORM: (state) => state.requestForm,
    CHANGES: (state) => state.changes,
    REQUESTS: (state) => state.requests,
    REQUESTDETAIL: (state) => state.requestDetail
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
    SET_CHANGES: (state, changes) => {
      state.changes = changes;
    },
    SET_REQUESTS: (state, requests) => {
      state.requests = requests;
    },
    SET_REQUESTDETAIL: (state, request) => {
      state.requestDetail = request;
    },
    RESET_FORM: (state) => {
      for(const p in defaultForm) {
        state.requestForm[p] = defaultForm[p];
      }
    },
  },
  actions: {
    GET_ROLES: async (context) => {
      const { data } = await axios.get('/api/lookup/roles');
      context.commit('SET_ROLES', data);
    },
    GET_USERS: async (context) => {
      const { data } = await axios.get('/api/users');
      context.commit('SET_USERS', data);
    },
    GET_REQUESTS: async (context) => {
      const { data } = await axios.get('/api/request');
      context.commit('SET_REQUESTS', data);
    },
    GET_REQUESTDETAIL: async (context, id) => {
      const { data } = await axios.get('/api/request/'+id);
      context.commit('SET_REQUESTDETAIL', data);
    },
    CLEAR_ERROR: (context) => {
      context.commit('SET_ERROR', null);
    },
    SET_STAGE: (context, stage) => {
      context.commit('SET_STAGE', stage);

      if(stage===2) {
        axios.post('/api/users/changes', context.state.users.filter(user => user.selected)).
          then((response) => {
            const changes = response.data;
            context.commit('SET_CHANGES', changes);
          });
      }
    },
    SUBMIT_REQUEST: (context) => {
      const users = context.state.users.filter(user => user.selected);
      const request = Object.assign({}, context.state.requestForm, {users});
      request.supporters = request.supporters.map(name => {return {name}});
      request.approvers = request.approvers.map(name => {return {name}});
      axios.post('/api/request', request).then(() => {
        context.dispatch('RESET');
      });
    },
    RESET: (context) => {
      context.dispatch('GET_USERS');
      context.dispatch('SET_STAGE', 0);
      context.commit('RESET_FORM', defaultForm);
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