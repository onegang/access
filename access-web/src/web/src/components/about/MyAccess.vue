<template>
  <div>
    <v-skeleton-loader v-if="MYACCESS===null" type="list-item-three-line" />
    <div v-if="MYACCESS">
      <UserDetails class="pa-4" :user="MYACCESS.user" readonly />
      <div class="pa-4">
        <h4>Requests</h4>
        <RequestList :requests="MYACCESS.requests" />
      </div>
      <v-divider></v-divider>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex';
import RequestList from '../requests/RequestList.vue';
import UserDetails from '../shared/UserDetails.vue';

export default {
  components: {
    RequestList,
    UserDetails,
  },
  mounted() {
    this.$store.dispatch('GET_MYACCESS');
  },
  computed: {
    ...mapGetters(['MYACCESS']),
  },
};
</script>

<style scoped>
  
</style>
