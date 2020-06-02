import Vue from 'vue';
import VueRouter from 'vue-router';
import Home from '../views/Home.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/page/requests',
    name: 'My Requests',
    component: () => import('../views/Requests.vue'),
  },
  {
    path: '/page/about',
    name: 'About',
    component: () => import('../views/About.vue'),
  },
  {
    path: '/page/requests/:id',
    name: 'Request Details',
    component: () => import('../views/RequestDetails.vue'),
  },
];

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

export default router;
