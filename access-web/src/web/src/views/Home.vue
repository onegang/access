<template>
  <div class="home">
    <div>
    </div>
    <v-badge
        :content="numberOfSelected"
        :value="numberOfSelected"
        :color="stage===0?'primary':'black'"
        overlap
        offset-x="25"
        offset-y="25"
      >
      <v-btn class="ma-2" outlined :color="stage===0?'primary':''" :disabled="disabled"
        @click="toAccessForm">New Request</v-btn>
    </v-badge>
    <div class="inline" v-if="stage>=1">
      <v-icon large>mdi-chevron-right</v-icon>
      <v-btn class="ma-2" outlined :color="stage===1?'primary':''"
          @click="toReviewForm">Review Request</v-btn>
    </div>
    <div class="inline" v-if="stage>=2">
      <v-icon large>mdi-chevron-right</v-icon>
      <v-btn class="ma-2" outlined :color="stage===2?'primary':''"
          @click="submitForm">Submit Request</v-btn>
    </div>
    <UsersTable msg="Welcome to My Access"/>
  </div>
</template>

<script>
// @ is an alias to /src
import UsersTable from '../components/UsersTable.vue';
import {mapGetters} from 'vuex';

export default {
  name: 'Home',
  components: {
    UsersTable,
  },
  computed: {
    stage() {
      return this.$store.getters.WORKFLOW.stage;
    },
    numberOfSelected() {
      return this.$store.getters.USERS.filter(user => user.selected).length;
    },
    disabled() {
      return this.numberOfSelected===0;
    },
    ...mapGetters(['USERS']),
  },
  methods: {
    toAccessForm() {
      if(this.stage!==0) {
        this.$store.dispatch('SET_STAGE', 0);
      } else {
        this.$store.dispatch('SET_STAGE', 1);
      }
    },
    toReviewForm() {
      if(this.stage!==1) {
        this.$store.dispatch('SET_STAGE', 1);
      } else {
        this.$store.dispatch('SET_STAGE', 2);
      }
    },
    submitForm() {
      console.log('TODO');
    }
  },
};
</script>

<style scoped>
  .inline {
    display: inline;
  }
</style>
