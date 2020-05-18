<template>
  <div class="home">
    <div>
    </div>
    <WorkflowButtons />
    <transition name="slide-left" mode="out-in">
      <UsersTable v-show="STAGE===0" />
    </transition>
    <transition name="slide-left" mode="out-in">
      <RequestDetails v-show="STAGE===1" />
    </transition>
    <transition name="slide-left" mode="out-in">
      <RequestSummary v-show="STAGE===2" />
    </transition>
  </div>
</template>

<script>

import WorkflowButtons from '../components/home/WorkflowButtons.vue';
import UsersTable from '../components/home/UsersTable.vue';
import RequestDetails from '../components/home/RequestDetails.vue';
import RequestSummary from '../components/home/RequestSummary.vue';
import {mapGetters} from 'vuex';

export default {
  name: 'Home',
  components: {
    WorkflowButtons,
    UsersTable,
    RequestDetails,
    RequestSummary,
  },
  computed: {
    ...mapGetters(['STAGE']),
  },
  methods: {
    
  },
};
</script>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition-duration: 0.5s;
  transition-property: height, opacity, transform;
  transition-timing-function: cubic-bezier(0.55, 0, 0.1, 1);
  overflow: hidden;
}

.slide-left-enter,
.slide-right-leave-active {
  opacity: 0;
  transform: translate(2em, 0);
}

.slide-left-leave-active,
.slide-right-enter {
  opacity: 0;
  transform: translate(-2em, 0);
}
</style>
