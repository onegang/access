<template>
  <div class="about">
    <UserDetails class="pa-4" :user="MYACCESS" readonly />
    <div class="pa-4">
      <h4>Requests</h4>
    </div>
    <v-divider></v-divider>
    <div class="float-right pa-6">
      <h5>{{SYSINFO.system}} {{SYSINFO.version}}</h5>
      <Fractals />
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex';
import Fractals from '../components/about/Fractals.vue';
import UserDetails from '../components/home/UserDetails.vue';

export default {
  name: 'About',
  components: {
    Fractals,
    UserDetails,
  },
  mounted() {
    this.$store.dispatch('GET_SYSINFO');
    this.$store.dispatch('GET_MYACCESS');
  },
  computed: {
    ...mapGetters(['SYSINFO', 'MYACCESS']),
  },
};
</script>
