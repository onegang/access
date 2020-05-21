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
      <v-btn class="ma-2" outlined :color="STAGE===0?'primary':''" :disabled="noSelection"
        @click="toAccessForm">New Request</v-btn>
    </v-badge>
    <div class="inline" v-if="STAGE>=1">
      <v-icon large>mdi-chevron-right</v-icon>
      <v-btn class="ma-2" outlined :color="STAGE===1?'primary':''"
          @click="toReviewForm">Submit Request</v-btn>
    </div>
    <div class="inline" v-if="STAGE>=2">
      <v-icon large>mdi-chevron-right</v-icon>
      <v-btn class="ma-2" outlined :color="STAGE===2?'primary':''" :disabled="!hasChanges"
          @click="submitForm">Confirm Request</v-btn>
    </div>
    <div class="inline" v-if="!noSelection">
      <v-tooltip right>
        <template v-slot:activator="{ on }">
          <v-btn v-on="on"
            class="mx-4"
            color="blue-grey lighten-4" fab x-small @click="clearForm">
            <v-icon>mdi-restore</v-icon>
          </v-btn>
        </template>
        <span>Clear and restart the request</span>
      </v-tooltip>
    </div>

    <div><h3>{{instructions}}</h3></div>

    <v-snackbar v-model="showSubmitted" top>
      <span>Submitted your request!</span>
      <v-btn
        text color="indigo"
        @click="showSubmitted = false">
        Close
      </v-btn>
    </v-snackbar>
  </div>
</template>

<script>

import {mapGetters} from 'vuex';

export default {
  mounted() {
    if(this.USERS.length===0)
      this.$store.dispatch('GET_USERS');
  },
  data: () => ({
    showSubmitted: false,
  }),
  computed: {
    numberOfSelected() {
      return this.USERS.filter(user => user.selected).length;
    },
    noSelection() {
      return this.numberOfSelected===0;
    },
    hasChanges() {
      if(this.CHANGES===null)
        return false;
      return this.CHANGES.added.length>0 || this.CHANGES.removed.length>0;
    },
    instructions() {
      if(this.STAGE===0) {
        return "Find and select the users for the access change request";
      } else if(this.STAGE===1) {
        return "Enter the details of the change";
      } else if(this.STAGE===2) {
        if(this.hasChanges)
          return "Review your request and then submit";
        else
          return "No access changes specified. Please go back and specify the changes.";
      } else {
        return "";
      }
    },
    ...mapGetters(['STAGE', 'USERS', 'CHANGES']),
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
      this.showSubmitted = true;
      this.$store.dispatch('SUBMIT_REQUEST');
      this.clearForm();
    },
    clearForm() {
      this.$store.dispatch('GET_USERS');
      this.$store.dispatch('SET_STAGE', 0);
    }
  },
};
</script>

<style scoped>
  .inline {
    display: inline;
  }
</style>
