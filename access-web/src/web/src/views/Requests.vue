<template>
  <div class="requests">
    <h1>My Requests</h1>
    <RequestList />
  </div>
</template>

<script>
import RequestList from '../components/requests/RequestList.vue';

export default {
  components: {
    RequestList,
  },
  mounted() {
    this.$store.dispatch('GET_REQUESTS');
  }
};
</script>