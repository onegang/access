<template>
<v-container>
  <v-skeleton-loader v-if="REQUESTS===null" type="list-item-three-line" />
  <v-data-iterator v-if="REQUESTS!==null"
    :items="REQUESTS"
    :page="page"
    no-data-text="No requests"
    sort-by="id"
    sort-desc
    hide-default-footer
  >
    <template v-slot:header>
      
    </template>
    <template v-slot:default="props">
      <v-row>
        <v-col
          v-for="item in props.items"
          :key="item.id"
          cols="12">
          <v-card ripple
            class="app-table-card">
            <v-card-title class="subtitle-1">
              <v-icon class="pr-2" v-if="item.selected">mdi-checkbox-marked-outline</v-icon>
              SR-{{item.id}}: {{ item.purpose }}
            </v-card-title>
            <v-divider></v-divider>

            <div class="body-2">
              <v-skeleton-loader type="list-item-two-line" />
            </div>
          </v-card>
        </v-col>
      </v-row>
    </template>

    <template v-slot:footer>
      <v-row class="mt-2" align="center" justify="center">
        <v-spacer></v-spacer>

        <span
          class="mr-4
          grey--text"
        >
          Page {{ page }} of {{ numberOfPages }}
        </span>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="mr-1"
          @click="formerPage"
        >
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="ml-1"
          @click="nextPage"
        >
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>
      </v-row>
    </template>
  </v-data-iterator>
</v-container>
</template>

<script>

  import {mapGetters} from 'vuex';

  export default {
    data() {
      return {
        page: 1,
        itemsPerPage: 10,
      };
    },
    computed: {
      numberOfPages() {
        return Math.ceil(this.REQUESTS.length / this.itemsPerPage);
      },
      ...mapGetters(['REQUESTS']),
    },
    methods: {
      nextPage() {
        if (this.page + 1 <= this.numberOfPages) this.page += 1;
      },
      formerPage() {
        if (this.page - 1 >= 1) this.page -= 1;
      },
    },
  };
</script>

<style scoped>
  
</style>
