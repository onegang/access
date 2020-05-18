<template>
  <div>
    <v-badge
        :content="numberOfSelected"
        :value="numberOfSelected"
        :color="STAGE===0?'primary':'black'"
        overlap
        offset-x="25"
        offset-y="25"
      >
      <v-btn class="ma-2" outlined :color="STAGE===0?'primary':''" :disabled="disabled"
        @click="toAccessForm">New Request</v-btn>
    </v-badge>
    <div class="inline" v-if="STAGE>=1">
      <v-icon large>mdi-chevron-right</v-icon>
      <v-btn class="ma-2" outlined :color="STAGE===1?'primary':''"
          @click="toReviewForm">Review Request</v-btn>
    </div>
    <div class="inline" v-if="STAGE>=2">
      <v-icon large>mdi-chevron-right</v-icon>
      <v-btn class="ma-2" outlined :color="STAGE===2?'primary':''"
          @click="submitForm">Submit Request</v-btn>
    </div>
  </div>
</template>

<script>

import {mapGetters} from 'vuex';

export default {
  computed: {
    numberOfSelected() {
      return this.USERS.filter(user => user.selected).length;
    },
    disabled() {
      return this.numberOfSelected===0;
    },
    ...mapGetters(['STAGE', 'USERS']),
  },
  methods: {
    toAccessForm() {
      if(this.STAGE!==0) {
        this.$store.dispatch('SET_STAGE', 0);
      } else {
        this.$store.dispatch('SET_STAGE', 1);
      }
    },
    toReviewForm() {
      if(this.STAGE!==1) {
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
