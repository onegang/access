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
    myaccess: null,
    sysinfo: {},
    roles: [],
    users: [],
    stage: 0,
    error: null,
    requestForm: Object.assign({}, defaultForm),
    changes: null,
    requests: null,
    requestDetail: null,
    requestActions: [],
  },
  getters: {
    getField,
    SYSINFO: (state) => state.sysinfo,
    MYACCESS: (state) => state.myaccess,
    ERROR: (state) => state.error,
    ROLES: (state) => state.roles,
    USERS: (state) => state.users,
    STAGE: (state) => state.stage,
    REQUESTFORM: (state) => state.requestForm,
    CHANGES: (state) => state.changes,
    REQUESTS: (state) => state.requests,
    REQUESTDETAIL: (state) => state.requestDetail,
    REQUESTACTIONS: (state) => state.requestActions
  },
  mutations: {
    updateField,
    SET_SYSINFO: (state, sysinfo) => {
      state.sysinfo = sysinfo;
    },
    SET_MYACCESS: (state, myaccess) => {
      state.myaccess = myaccess;
    },
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
    SET_REQUESTACTIONS: (state, actions) => {
      state.requestActions = actions;
    },
    RESET_FORM: (state) => {
      for(const p in defaultForm) {
        state.requestForm[p] = defaultForm[p];
      }
    },
  },
  actions: {
    GET_SYSINFO: async (context) => {
      const { data } = await axios.get('/api/lookup/sysinfo');
      context.commit('SET_SYSINFO', data);
    },
    GET_MYACCESS: async (context) => {
      const { data } = await axios.get('/api/users/me');
      context.commit('SET_MYACCESS', data);
    },
    GET_ROLES: async (context) => {
      const { data } = await axios.get('/api/lookup/roles');
      context.commit('SET_ROLES', data);
    },
    GET_USERS: async (context) => {
      const { data } = await axios.get('/api/users');
      context.commit('SET_USERS', data);
    },
    GET_REQUESTS: async (context, filter) => {
      const { data } = await axios.get('/api/request/filter/'+filter);
      context.commit('SET_REQUESTS', data);
    },
    GET_REQUESTDETAIL: async (context, id) => {
      const { data } = await axios.get('/api/request/'+id);
      context.commit('SET_REQUESTDETAIL', data);
    },
    GET_REQUESTACTIONS: async (context, id) => {
      const { data } = await axios.get('/api/request/'+id+'/actions');
      context.commit('SET_REQUESTACTIONS', data);
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
    },
    DO_ACTION: async (context, actionInfo) => {
      const id = actionInfo.id;
      const action = actionInfo.action;
      axios.post('/api/request/'+id+'/action/'+action).
          then((response) => {
            const request = response.data;
            context.commit('SET_REQUESTDETAIL', request);
            context.dispatch('GET_REQUESTACTIONS', id);
      });
    },
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