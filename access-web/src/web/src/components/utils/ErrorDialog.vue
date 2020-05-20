<template>
  <v-dialog
      v-model="showError"
      width="500"
    >
      <v-card>
        <v-card-title
          class="headline"
          primary-title
        >
          Error
        </v-card-title>

        <v-card-text>
          {{ERROR}}
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            text
            @click="dismiss"
          >
            OK
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
</template>

<script>
  import {mapGetters} from 'vuex';

  export default {
    computed: {
      showError: {
        get()
        {
            return this.ERROR!=null;
        },
        set()
        {
            this.$store.dispatch('CLEAR_ERROR');
        }
      },
      ...mapGetters(['ERROR']),
    },
    methods: {
      dismiss() {
        this.$store.dispatch('CLEAR_ERROR');
      }
    }
  };
</script>

<style scoped>
  
</style>
