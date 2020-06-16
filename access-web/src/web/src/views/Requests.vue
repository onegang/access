<template>
  <v-container class="requests">
    <v-row>
      <v-col cols="3">
        <h2>My Requests</h2>
      </v-col>
      <v-col cols="9">
        <v-btn-toggle v-model="filter" mandatory light class="float-right" @change="changeFilter">
          <v-btn value="FOR_ACTION">
            For Action
          </v-btn>
          <v-btn value="OPENED">
            Opened
          </v-btn>
          <v-btn value="CLOSED">
            Closed
          </v-btn>
        </v-btn-toggle>
      </v-col>
    </v-row>
    <RequestList :requests="REQUESTS" />
  </v-container>
</template>

<script>
import {mapGetters} from 'vuex';
import RequestList from '../components/requests/RequestList.vue';

export default {
  components: {
    RequestList,
  },
  data() {
    return {
      filter: "FOR_ACTION",
    };
  },
  computed: {
    ...mapGetters(['REQUESTS']),
  },
  mounted() {
     this.$store.dispatch('GET_REQUESTS', this.filter);
  },
  methods: {
    changeFilter() {
      this.$store.dispatch('GET_REQUESTS', this.filter);
    },
  }
};
</script>