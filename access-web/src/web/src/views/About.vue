<template>
  <div class="about">
    <MyAccess />
    <div class="float-right pa-6">
      <h5>{{SYSINFO.system}} {{SYSINFO.version}}</h5>
      <Fractals />
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex';
import Fractals from '../components/about/Fractals.vue';
import MyAccess from '../components/about/MyAccess.vue';

export default {
  name: 'About',
  components: {
    Fractals,
    MyAccess,
  },
  mounted() {
    this.$store.dispatch('GET_SYSINFO');
  },
  computed: {
    ...mapGetters(['SYSINFO']),
  },
};
</script>
