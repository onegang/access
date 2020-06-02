<template>
  <div class="requests">
    <h1>Request SR-{{$route.params.id}}</h1>
    <v-divider />
    <RequestDetailInfo v-if="REQUESTDETAIL!=null" :request="REQUESTDETAIL" :actions="REQUESTACTIONS"/>
  </div>
</template>

<script>

import {mapGetters} from 'vuex';
import RequestDetailInfo from '../components/requests/RequestDetailInfo';

export default {
  components: {
    RequestDetailInfo,
  },
  computed: {
      ...mapGetters(['REQUESTDETAIL', 'REQUESTACTIONS']),
    },
  mounted() {
    this.$store.dispatch('GET_REQUESTDETAIL', this.$route.params.id);
    this.$store.dispatch('GET_REQUESTACTIONS', this.$route.params.id);
  }
};
</script>